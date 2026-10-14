const fs = require('fs');
const { randomUUID } = require('crypto');
const mysql = require('mysql2/promise');

if (!process.env.DATABASE_URL) {
  throw new Error('DATABASE_URL must be set to connect to MySQL.');
}

const databaseUrl = new URL(process.env.DATABASE_URL);
if (databaseUrl.protocol !== 'mysql:') {
  throw new Error('DATABASE_URL must use the mysql:// scheme.');
}

const ssl = process.env.MYSQL_CA_CERT
  ? { ca: fs.readFileSync(process.env.MYSQL_CA_CERT, 'utf8') }
  : { rejectUnauthorized: false };

const pool = mysql.createPool({
  host: databaseUrl.hostname,
  port: Number(databaseUrl.port) || 3306,
  user: decodeURIComponent(databaseUrl.username),
  password: decodeURIComponent(databaseUrl.password),
  database: decodeURIComponent(databaseUrl.pathname.slice(1)),
  ssl,
  waitForConnections: true,
  connectionLimit: 10,
  connectTimeout: 10000,
  timezone: 'Z',
});

const one = async (sql, values = []) => {
  const [rows] = await pool.execute(sql, values);
  return rows[0] || null;
};

const many = async (sql, values = []) => {
  const [rows] = await pool.execute(sql, values);
  return rows;
};

const toApiRow = (row) => {
  if (!row) return null;
  return Object.fromEntries(
    Object.entries(row).map(([key, value]) => {
      if (key === 'id') return ['_id', value];
      if (key === 'user_id') return ['user', value];
      return [key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase()), value];
    }),
  );
};

const toApiRows = (rows) => rows.map(toApiRow);

const schema = [
  `CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    college VARCHAR(255) NOT NULL DEFAULT '',
    degree VARCHAR(255) NOT NULL DEFAULT '',
    branch VARCHAR(255) NOT NULL DEFAULT '',
    graduation_year INT NOT NULL DEFAULT 2026,
    role VARCHAR(32) NOT NULL DEFAULT 'user',
    skills JSON NOT NULL,
    profile_image TEXT NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
  ) ENGINE=InnoDB`,
  `CREATE TABLE IF NOT EXISTS interviews (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    type VARCHAR(100) NOT NULL,
    difficulty VARCHAR(100) NOT NULL,
    questions JSON NOT NULL,
    score INT NOT NULL DEFAULT 0,
    feedback JSON NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'completed',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY interviews_user_created_idx (user_id, created_at),
    CONSTRAINT interviews_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  ) ENGINE=InnoDB`,
  `CREATE TABLE IF NOT EXISTS questions (
    id CHAR(36) PRIMARY KEY,
    question TEXT NOT NULL,
    type VARCHAR(100) NOT NULL DEFAULT 'Technical',
    category VARCHAR(100) NOT NULL DEFAULT 'General',
    difficulty VARCHAR(32) NOT NULL DEFAULT 'Medium',
    answer TEXT NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
  ) ENGINE=InnoDB`,
  `CREATE TABLE IF NOT EXISTS resume_analyses (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    score INT NOT NULL DEFAULT 0,
    job_title VARCHAR(255) NOT NULL DEFAULT '',
    summary TEXT NOT NULL,
    strengths JSON NOT NULL,
    weaknesses JSON NOT NULL,
    suggestions JSON NOT NULL,
    categories JSON NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY resume_analyses_user_created_idx (user_id, created_at),
    CONSTRAINT resume_analyses_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  ) ENGINE=InnoDB`,
  `CREATE TABLE IF NOT EXISTS coding_submissions (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    problem_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    language VARCHAR(100) NOT NULL DEFAULT 'JavaScript',
    difficulty VARCHAR(32) NOT NULL DEFAULT 'Medium',
    code LONGTEXT NOT NULL,
    attempted BOOLEAN NOT NULL DEFAULT TRUE,
    solved BOOLEAN NOT NULL DEFAULT FALSE,
    score INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY coding_submissions_user_idx (user_id),
    CONSTRAINT coding_submissions_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  ) ENGINE=InnoDB`,
  `CREATE TABLE IF NOT EXISTS progress (
    user_id CHAR(36) PRIMARY KEY,
    interview_scores JSON NOT NULL,
    resume_score INT NOT NULL DEFAULT 0,
    coding_solved INT NOT NULL DEFAULT 0,
    streak INT NOT NULL DEFAULT 0,
    skill_progress JSON NOT NULL,
    weekly_activity JSON NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT progress_user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  ) ENGINE=InnoDB`,
];

async function initializeDatabase() {
  for (const statement of schema) await pool.query(statement);
}

const userColumns = '`id`, `name`, `email`, `password`, `college`, `degree`, `branch`, `graduation_year`, `role`, `skills`, `profile_image`, `created_at`, `updated_at`';

async function findUserByEmail(email) {
  return toApiRow(await one(`SELECT ${userColumns} FROM users WHERE LOWER(email) = LOWER(?)`, [email]));
}

async function findUserById(id, includePassword = false) {
  const user = toApiRow(await one(`SELECT ${userColumns} FROM users WHERE id = ?`, [id]));
  if (!user || includePassword) return user;
  const { password, ...safeUser } = user;
  return safeUser;
}

async function createUser(user) {
  const id = randomUUID();
  await pool.execute(
    `INSERT INTO users (id, name, email, password, college, degree, branch, graduation_year, role, skills, profile_image)
     VALUES (?, ?, LOWER(?), ?, ?, ?, ?, ?, ?, ?, ?)`,
    [id, user.name, user.email, user.password, user.college || '', user.degree || '', user.branch || '',
      user.graduationYear || new Date().getFullYear(), user.role || 'user', JSON.stringify(user.skills || []), user.profileImage || ''],
  );
  return toApiRow(await one(`SELECT ${userColumns} FROM users WHERE id = ?`, [id]));
}

async function updateUser(id, values) {
  const columns = {
    name: 'name', college: 'college', degree: 'degree', branch: 'branch',
    graduationYear: 'graduation_year', skills: 'skills', role: 'role',
  };
  const assignments = [];
  const parameters = [];
  Object.entries(values).forEach(([key, value]) => {
    const column = columns[key];
    if (!column) return;
    parameters.push(key === 'skills' ? JSON.stringify(value) : value);
    assignments.push(`\`${column}\` = ?`);
  });
  if (!assignments.length) return findUserById(id);
  parameters.push(id);
  await pool.execute(`UPDATE users SET ${assignments.join(', ')}, updated_at = CURRENT_TIMESTAMP(3) WHERE id = ?`, parameters);
  return findUserById(id);
}

async function setUserPassword(id, password) {
  await pool.execute('UPDATE users SET password = ?, updated_at = CURRENT_TIMESTAMP(3) WHERE id = ?', [password, id]);
}

async function deleteUser(id) {
  const [result] = await pool.execute('DELETE FROM users WHERE id = ?', [id]);
  return result.affectedRows > 0;
}

async function listUsers() {
  const rows = toApiRows(await many(`SELECT ${userColumns} FROM users ORDER BY created_at DESC`));
  return rows.map(({ password, ...user }) => user);
}

async function count(table) {
  const allowed = new Set(['users', 'questions', 'interviews']);
  if (!allowed.has(table)) throw new Error('Invalid count table.');
  const result = await one(`SELECT COUNT(*) AS count FROM \`${table}\``);
  return Number(result.count);
}

async function createQuestion(question) {
  const id = randomUUID();
  await pool.execute(
    `INSERT INTO questions (id, question, type, category, difficulty, answer) VALUES (?, ?, ?, ?, ?, ?)`,
    [id, question.question, question.type || 'Technical', question.category || 'General', question.difficulty || 'Medium', question.answer || ''],
  );
  return toApiRow(await one('SELECT * FROM questions WHERE id = ?', [id]));
}

async function seedQuestions(questions) {
  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();
    const [rows] = await connection.query('SELECT COUNT(*) AS count FROM questions');
    if (Number(rows[0].count) === 0) {
      for (const question of questions) {
        await connection.execute(
          `INSERT INTO questions (id, question, type, category, difficulty, answer) VALUES (?, ?, ?, ?, ?, ?)`,
          [randomUUID(), question.question, question.type || 'Technical', question.category || 'General', question.difficulty || 'Medium', question.answer || ''],
        );
      }
    }
    await connection.commit();
  } catch (error) {
    await connection.rollback();
    throw error;
  } finally {
    connection.release();
  }
}

async function findQuestions({ type, difficulty, search }) {
  const conditions = [];
  const values = [];
  if (type && type !== 'All') {
    conditions.push('(LOWER(type) = LOWER(?) OR LOWER(category) = LOWER(?))');
    values.push(type, type);
  }
  if (difficulty && difficulty !== 'All') {
    conditions.push('LOWER(difficulty) = LOWER(?)');
    values.push(difficulty);
  }
  if (search) {
    conditions.push('question LIKE ?');
    values.push(`%${search}%`);
  }
  const where = conditions.length ? `WHERE ${conditions.join(' AND ')}` : '';
  return toApiRows(await many(`SELECT * FROM questions ${where} ORDER BY created_at DESC`, values));
}

async function findQuestionById(id) {
  return toApiRow(await one('SELECT * FROM questions WHERE id = ?', [id]));
}

async function updateQuestion(id, question) {
  await pool.execute(
    `UPDATE questions SET question = COALESCE(?, question), type = COALESCE(?, type),
      category = COALESCE(?, category), difficulty = COALESCE(?, difficulty),
      answer = COALESCE(?, answer), updated_at = CURRENT_TIMESTAMP(3) WHERE id = ?`,
    [question.question ?? null, question.type ?? null, question.category ?? null,
      question.difficulty ?? null, question.answer ?? null, id],
  );
  return findQuestionById(id);
}

async function deleteQuestion(id) {
  const [result] = await pool.execute('DELETE FROM questions WHERE id = ?', [id]);
  return result.affectedRows > 0;
}

async function createInterview(interview) {
  const id = randomUUID();
  await pool.execute(
    `INSERT INTO interviews (id, user_id, type, difficulty, questions, score, feedback, status)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
    [id, interview.user, interview.type, interview.difficulty, JSON.stringify(interview.questions), interview.score,
      JSON.stringify(interview.feedback), interview.status || 'completed'],
  );
  return toApiRow(await one('SELECT * FROM interviews WHERE id = ?', [id]));
}

async function findInterviewsByUser(userId) {
  return toApiRows(await many('SELECT * FROM interviews WHERE user_id = ? ORDER BY created_at DESC', [userId]));
}

async function findInterviewByIdForUser(id, userId) {
  return toApiRow(await one('SELECT * FROM interviews WHERE id = ? AND user_id = ?', [id, userId]));
}

async function deleteInterview(id, userId) {
  const [result] = await pool.execute('DELETE FROM interviews WHERE id = ? AND user_id = ?', [id, userId]);
  return result.affectedRows > 0;
}

async function averageInterviewScore() {
  const result = await one('SELECT AVG(score) AS average FROM interviews');
  return Number(result.average || 0);
}

async function createResumeAnalysis(analysis) {
  const id = randomUUID();
  await pool.execute(
    `INSERT INTO resume_analyses (id, user_id, score, job_title, summary, strengths, weaknesses, suggestions, categories)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [id, analysis.user, analysis.score, analysis.jobTitle, analysis.summary,
      JSON.stringify(analysis.strengths), JSON.stringify(analysis.weaknesses), JSON.stringify(analysis.suggestions), JSON.stringify(analysis.categories)],
  );
  return toApiRow(await one('SELECT * FROM resume_analyses WHERE id = ?', [id]));
}

async function findResumeAnalysesByUser(userId) {
  return toApiRows(await many('SELECT * FROM resume_analyses WHERE user_id = ? ORDER BY created_at DESC', [userId]));
}

async function createCodingSubmission(submission) {
  const id = randomUUID();
  await pool.execute(
    `INSERT INTO coding_submissions (id, user_id, problem_id, title, language, difficulty, code, attempted, solved, score)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [id, submission.user, submission.problemId, submission.title, submission.language, submission.difficulty,
      submission.code, submission.attempted, submission.solved, submission.score],
  );
  return toApiRow(await one('SELECT * FROM coding_submissions WHERE id = ?', [id]));
}

async function countSolvedSubmissions(userId) {
  const result = await one('SELECT COUNT(*) AS count FROM coding_submissions WHERE user_id = ? AND solved = TRUE', [userId]);
  return Number(result.count);
}

async function getProgress(userId) {
  return toApiRow(await one('SELECT * FROM progress WHERE user_id = ?', [userId]));
}

async function saveProgress(userId, progress) {
  await pool.execute(
    `INSERT INTO progress (user_id, interview_scores, resume_score, coding_solved, streak, skill_progress, weekly_activity)
     VALUES (?, ?, ?, ?, ?, ?, ?)
     ON DUPLICATE KEY UPDATE interview_scores = VALUES(interview_scores), resume_score = VALUES(resume_score),
       coding_solved = VALUES(coding_solved), streak = VALUES(streak), skill_progress = VALUES(skill_progress),
       weekly_activity = VALUES(weekly_activity), updated_at = CURRENT_TIMESTAMP(3)`,
    [userId, JSON.stringify(progress.interviewScores || []), progress.resumeScore || 0,
      progress.codingSolved || 0, progress.streak || 0, JSON.stringify(progress.skillProgress || {}),
      JSON.stringify(progress.weeklyActivity || [0, 0, 0, 0, 0, 0, 0])],
  );
  return getProgress(userId);
}

module.exports = {
  initializeDatabase,
  findUserByEmail,
  findUserById,
  createUser,
  updateUser,
  setUserPassword,
  deleteUser,
  listUsers,
  count,
  createQuestion,
  seedQuestions,
  findQuestions,
  findQuestionById,
  updateQuestion,
  deleteQuestion,
  createInterview,
  findInterviewsByUser,
  findInterviewByIdForUser,
  deleteInterview,
  averageInterviewScore,
  createResumeAnalysis,
  findResumeAnalysesByUser,
  createCodingSubmission,
  countSolvedSubmissions,
  getProgress,
  saveProgress,
};
