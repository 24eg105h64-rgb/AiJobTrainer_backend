const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '.env') });

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET;
const allowedOrigins = new Set([
  'http://localhost:5173',
  'http://127.0.0.1:5173',
  ...(process.env.FRONTEND_URL || '')
    .split(',')
    .map((origin) => origin.trim())
    .filter(Boolean),
]);

if (!JWT_SECRET || Buffer.byteLength(JWT_SECRET, 'utf8') < 32) {
  throw new Error('JWT_SECRET must be set to at least 32 bytes.');
}

app.use(
  cors({
    origin: (origin, callback) => callback(null, !origin || allowedOrigins.has(origin)),
    credentials: true,
  }),
);
app.use(express.json({ limit: '10mb' }));

const userSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    password: { type: String, required: true, minlength: 6 },
    college: { type: String, default: '' },
    degree: { type: String, default: '' },
    branch: { type: String, default: '' },
    graduationYear: { type: Number, default: new Date().getFullYear() },
    role: { type: String, enum: ['user', 'admin'], default: 'user' },
    skills: { type: [String], default: [] },
    profileImage: { type: String, default: '' },
  },
  { timestamps: true },
);

const interviewSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    type: { type: String, required: true },
    difficulty: { type: String, required: true },
    questions: [
      {
        text: { type: String, required: true },
        answer: { type: String, default: '' },
      },
    ],
    score: { type: Number, default: 0 },
    feedback: {
      overall: { type: Number, default: 0 },
      communication: { type: Number, default: 0 },
      technical: { type: Number, default: 0 },
      confidence: { type: Number, default: 0 },
      problemSolving: { type: Number, default: 0 },
      strengths: [String],
      weaknesses: [String],
      suggestions: [String],
    },
    status: { type: String, default: 'completed' },
  },
  { timestamps: true },
);

const questionSchema = new mongoose.Schema(
  {
    question: { type: String, required: true },
    type: { type: String, enum: ['Technical', 'HR', 'Behavioral', 'DSA', 'SQL', 'Aptitude'], default: 'Technical' },
    category: { type: String, default: 'General' },
    difficulty: { type: String, enum: ['Easy', 'Medium', 'Hard'], default: 'Medium' },
    answer: { type: String, default: '' },
  },
  { timestamps: true },
);

const resumeAnalysisSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    score: { type: Number, default: 0 },
    jobTitle: { type: String, default: '' },
    summary: { type: String, default: '' },
    strengths: [String],
    weaknesses: [String],
    suggestions: [String],
    categories: {
      skills: { type: Number, default: 0 },
      education: { type: Number, default: 0 },
      projects: { type: Number, default: 0 },
      experience: { type: Number, default: 0 },
      formatting: { type: Number, default: 0 },
      keywords: { type: Number, default: 0 },
    },
  },
  { timestamps: true },
);

const codingSubmissionSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    problemId: { type: String, required: true },
    title: { type: String, required: true },
    language: { type: String, default: 'JavaScript' },
    difficulty: { type: String, default: 'Medium' },
    code: { type: String, default: '' },
    attempted: { type: Boolean, default: true },
    solved: { type: Boolean, default: false },
    score: { type: Number, default: 0 },
  },
  { timestamps: true },
);

const progressSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, unique: true },
    interviewScores: { type: [Number], default: [] },
    resumeScore: { type: Number, default: 0 },
    codingSolved: { type: Number, default: 0 },
    streak: { type: Number, default: 0 },
    skillProgress: { type: Object, default: {} },
    weeklyActivity: { type: [Number], default: [0, 0, 0, 0, 0, 0, 0] },
  },
  { timestamps: true },
);

userSchema.pre('save', async function save() {
  if (!this.isModified('password')) return;
  this.password = await bcrypt.hash(this.password, 10);
});

userSchema.methods.comparePassword = async function comparePassword(candidate) {
  return bcrypt.compare(candidate, this.password);
};

const User = mongoose.model('User', userSchema);
const Interview = mongoose.model('Interview', interviewSchema);
const Question = mongoose.model('Question', questionSchema);
const ResumeAnalysis = mongoose.model('ResumeAnalysis', resumeAnalysisSchema);
const CodingSubmission = mongoose.model('CodingSubmission', codingSubmissionSchema);
const Progress = mongoose.model('Progress', progressSchema);

const codingProblems = [
  {
    _id: 'prob-1',
    title: 'Two Sum',
    category: 'DSA',
    difficulty: 'Easy',
    description: 'Given an array of integers and a target value, return the indices of the two numbers that add up to the target.',
    input: 'nums = [2, 7, 11, 15], target = 9',
    output: '[0, 1]',
    examples: ['Example 1: Input: [2,7,11,15], 9 -> Output: [0,1]'],
    constraints: 'Use a hash map or two-pointer approach.',
  },
  {
    _id: 'prob-2',
    title: 'Palindrome Check',
    category: 'JavaScript',
    difficulty: 'Easy',
    description: 'Determine whether a string is a palindrome ignoring spaces and case.',
    input: 'A man, a plan, a canal: Panama',
    output: 'true',
    examples: ['Example 1: Input: "racecar" -> Output: true'],
    constraints: 'Keep the solution O(n).',
  },
  {
    _id: 'prob-3',
    title: 'Longest Substring Without Repeating Characters',
    category: 'Python',
    difficulty: 'Medium',
    description: 'Find the length of the longest substring without repeating characters.',
    input: 'abcabcbb',
    output: '3',
    examples: ['Example 1: Input: "abcabcbb" -> Output: 3'],
    constraints: 'Use sliding window.',
  },
];

const fallbackQuestions = [
  { question: 'Tell me about yourself and your career goals.', type: 'HR', difficulty: 'Easy', answer: '' },
  { question: 'Explain a project you are proud of and the challenges you solved.', type: 'Technical', difficulty: 'Medium', answer: '' },
  { question: 'How do you handle deadlines and pressure during placements?', type: 'Behavioral', difficulty: 'Medium', answer: '' },
  { question: 'What is the difference between SQL and NoSQL?', type: 'SQL', difficulty: 'Medium', answer: '' },
  { question: 'Describe a time you worked in a team and resolved a conflict.', type: 'Behavioral', difficulty: 'Hard', answer: '' },
  { question: 'What is the time complexity of a hash map lookup?', type: 'DSA', difficulty: 'Hard', answer: '' },
];

const createToken = (user) =>
  jwt.sign({ id: user._id, role: user.role }, JWT_SECRET, { expiresIn: '7d' });

const auth = async (req, res, next) => {
  const header = req.headers.authorization || '';
  const token = header.startsWith('Bearer ') ? header.split(' ')[1] : null;

  if (!token) {
    return res.status(401).json({ message: 'Authentication required.' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const user = await User.findById(decoded.id).select('-password');

    if (!user) {
      return res.status(401).json({ message: 'User not found.' });
    }

    req.user = user;
    next();
  } catch (error) {
    return res.status(401).json({ message: 'Invalid or expired token.' });
  }
};

const adminOnly = (req, res, next) => {
  if (!req.user || req.user.role !== 'admin') {
    return res.status(403).json({ message: 'Admin access required.' });
  }
  next();
};

const buildProgress = async (userId) => {
  const interviews = await Interview.find({ user: userId }).sort({ createdAt: -1 });
  const analyses = await ResumeAnalysis.find({ user: userId }).sort({ createdAt: -1 });
  const submissions = await CodingSubmission.find({ user: userId, solved: true });

  const interviewScores = interviews.map((item) => item.score || 0);
  const averageInterviewScore = interviewScores.length
    ? Math.round(interviewScores.reduce((sum, score) => sum + score, 0) / interviewScores.length)
    : 0;

  const lastResume = analyses[0]?.score || 0;
  const codingSolved = submissions.length;

  return {
    interviewsCompleted: interviews.length,
    averageInterviewScore,
    resumeScore: lastResume,
    codingSolved,
    currentStreak: Math.max(1, Math.min(7, interviews.length || 1)),
    skillProgress: {
      Communication: averageInterviewScore,
      Technical: Math.min(100, averageInterviewScore + 10),
      Resume: lastResume,
      Coding: Math.min(100, codingSolved * 18),
    },
    weeklyActivity: [2, 4, 5, 3, 6, 2, 1],
  };
};

const mockInterviewFeedback = (score, type, difficulty) => {
  const categories = {
    communication: Math.max(60, Math.min(98, score + 8)),
    technical: Math.max(55, Math.min(96, score + 4)),
    confidence: Math.max(58, Math.min(97, score + 10)),
    problemSolving: Math.max(60, Math.min(95, score + 6)),
  };

  return {
    overall: score,
    communication: categories.communication,
    technical: categories.technical,
    confidence: categories.confidence,
    problemSolving: categories.problemSolving,
    strengths: [`Strong ${type.toLowerCase()} readiness`, `Good ${difficulty.toLowerCase()} preparation`, 'Clear learning mindset'],
    weaknesses: ['Improve detail depth', 'Add more examples', 'Refine answer structure'],
    suggestions: ['Practice with a timed mock interview', 'Review common technical questions', 'Use STAR method in behavioral answers'],
  };
};

const generateResumeAnalysis = (summary = '', jobTitle = '', skills = []) => {
  const keywords = ['react', 'node', 'mongodb', 'javascript', 'api', 'teamwork', 'problem solving', 'communication'];
  const matched = keywords.filter((word) => `${summary} ${skills.join(' ')}`.toLowerCase().includes(word));
  const score = Math.min(99, 55 + matched.length * 7 + (jobTitle ? 5 : 0));

  return {
    score: Math.round(score),
    strengths: [
      'Strong foundation in core technical concepts',
      'Clear interest in job preparation and learning',
      'Good combination of communication and project focus',
    ],
    weaknesses: ['Add more measurable project outcomes', 'Include quantifiable metrics', 'Highlight key technologies more clearly'],
    suggestions: ['Add project impact numbers', 'List relevant tools and frameworks', 'Keep formatting consistent and concise'],
    categories: {
      skills: Math.min(100, 68 + matched.length * 5),
      education: 78,
      projects: 80,
      experience: 75,
      formatting: 82,
      keywords: Math.min(100, 60 + matched.length * 8),
    },
  };
};

const seedAdmin = async () => {
  const adminEmail = process.env.ADMIN_EMAIL?.trim().toLowerCase();
  const adminPassword = process.env.ADMIN_PASSWORD;
  if (!adminEmail || !adminPassword) return;

  const exists = await User.findOne({ email: adminEmail });
  if (!exists) {
    await User.create({
      name: 'Admin User',
      email: adminEmail,
      password: adminPassword,
      college: 'AI University',
      degree: 'B.Tech',
      branch: 'Computer Science',
      graduationYear: 2026,
      role: 'admin',
      skills: ['React', 'Node', 'MongoDB'],
    });
  }
};

const seedQuestions = async () => {
  const count = await Question.countDocuments();
  if (count > 0) return;

  await Question.insertMany([
    { question: 'What is the difference between let and var in JavaScript?', type: 'Technical', category: 'JavaScript', difficulty: 'Easy', answer: 'let has block scope while var has function scope.' },
    { question: 'What is a REST API?', type: 'Technical', category: 'Web Development', difficulty: 'Medium', answer: 'REST stands for Representational State Transfer and uses HTTP methods to exchange resources.' },
    { question: 'Explain the STAR method.', type: 'HR', category: 'Interview', difficulty: 'Easy', answer: 'Situation, Task, Action, Result describes a structured interview answer.' },
    { question: 'What are joins in SQL?', type: 'SQL', category: 'Database', difficulty: 'Medium', answer: 'Joins combine rows from two or more tables.' },
    { question: 'How would you explain recursion to a beginner?', type: 'DSA', category: 'Algorithms', difficulty: 'Medium', answer: 'Recursion is a function calling itself to solve smaller instances of the same problem.' },
  ]);
};

app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', message: 'AI Job Trainer API is running.' });
});

app.post('/api/auth/register', async (req, res) => {
  try {
    const { name, email, password, confirmPassword, college, degree, branch, graduationYear } = req.body;

    if (!name || !email || !password || !college || !degree || !branch) {
      return res.status(400).json({ message: 'All required fields are required.' });
    }
    if (password.length < 6) {
      return res.status(400).json({ message: 'Password must be at least 6 characters long.' });
    }
    if (password !== confirmPassword) {
      return res.status(400).json({ message: 'Passwords do not match.' });
    }

    const existing = await User.findOne({ email: email.toLowerCase() });
    if (existing) {
      return res.status(400).json({ message: 'This email is already registered.' });
    }

    const user = await User.create({
      name,
      email: email.toLowerCase(),
      password,
      college,
      degree,
      branch,
      graduationYear,
      role: 'user',
      skills: [],
    });

    const token = createToken(user);
    const safeUser = { _id: user._id, name: user.name, email: user.email, role: user.role, college: user.college, degree: user.degree, branch: user.branch, graduationYear: user.graduationYear, skills: user.skills };

    res.status(201).json({ message: 'Registration successful.', token, user: safeUser });
  } catch (error) {
    res.status(500).json({ message: error.message || 'Registration failed.' });
  }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ message: 'Email and password are required.' });
    }

    const user = await User.findOne({ email: email.toLowerCase() });
    if (!user) {
      return res.status(400).json({ message: 'Invalid email or password.' });
    }

    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      return res.status(400).json({ message: 'Invalid email or password.' });
    }

    const token = createToken(user);
    const safeUser = { _id: user._id, name: user.name, email: user.email, role: user.role, college: user.college, degree: user.degree, branch: user.branch, graduationYear: user.graduationYear, skills: user.skills };

    res.json({ message: 'Login successful.', token, user: safeUser });
  } catch (error) {
    res.status(500).json({ message: error.message || 'Login failed.' });
  }
});

app.get('/api/auth/me', auth, async (req, res) => {
  res.json({ user: req.user });
});

app.get('/api/users/profile', auth, async (req, res) => {
  const user = await User.findById(req.user._id).select('-password');
  res.json({ user });
});

app.put('/api/users/profile', auth, async (req, res) => {
  const allowed = ['name', 'college', 'degree', 'branch', 'graduationYear', 'skills'];
  const updates = {};

  allowed.forEach((field) => {
    if (req.body[field] !== undefined) updates[field] = req.body[field];
  });

  const user = await User.findByIdAndUpdate(req.user._id, updates, { new: true }).select('-password');
  res.json({ message: 'Profile updated successfully.', user });
});

app.put('/api/users/password', auth, async (req, res) => {
  const { currentPassword, newPassword } = req.body;
  if (!currentPassword || !newPassword) {
    return res.status(400).json({ message: 'Current password and new password are required.' });
  }

  const user = await User.findById(req.user._id);
  const isMatch = await user.comparePassword(currentPassword);
  if (!isMatch) {
    return res.status(400).json({ message: 'Current password is incorrect.' });
  }

  user.password = newPassword;
  await user.save();
  res.json({ message: 'Password updated successfully.' });
});

app.delete('/api/users/account', auth, async (req, res) => {
  await User.findByIdAndDelete(req.user._id);
  await Interview.deleteMany({ user: req.user._id });
  await ResumeAnalysis.deleteMany({ user: req.user._id });
  await CodingSubmission.deleteMany({ user: req.user._id });
  await Progress.deleteMany({ user: req.user._id });
  res.json({ message: 'Account deleted successfully.' });
});

app.post('/api/interviews', auth, async (req, res) => {
  try {
    const { type = 'Technical', difficulty = 'Medium', questionCount = 5, questions = [], answers = [] } = req.body;

    const generatedQuestions = questions.length
      ? questions.map((item) => ({ text: item.question || item.text || 'Interview question', answer: item.answer || '' }))
      : fallbackQuestions
          .filter((item) => item.type === type || type === 'Mixed' || item.type === 'Technical')
          .slice(0, questionCount)
          .map((item) => ({ text: item.question, answer: '' }));

    const entries = generatedQuestions.map((question, index) => ({
      text: question.text,
      answer: answers[index] || question.answer || '',
    }));

    const score = Math.max(55, Math.min(98, 60 + entries.filter((entry) => entry.answer && entry.answer.length > 20).length * 8));
    const feedback = mockInterviewFeedback(score, type, difficulty);

    const interview = await Interview.create({
      user: req.user._id,
      type,
      difficulty,
      questions: entries,
      score,
      feedback,
      status: 'completed',
    });

    const progress = await Progress.findOne({ user: req.user._id });
    if (progress) {
      progress.interviewScores.push(score);
      await progress.save();
    } else {
      await Progress.create({
        user: req.user._id,
        interviewScores: [score],
        resumeScore: 0,
        codingSolved: 0,
        streak: 1,
        skillProgress: {},
        weeklyActivity: [0, 0, 0, 0, 0, 0, 0],
      });
    }

    res.status(201).json({ message: 'Interview submitted successfully.', interview });
  } catch (error) {
    res.status(500).json({ message: error.message || 'Interview submission failed.' });
  }
});

app.get('/api/interviews', auth, async (req, res) => {
  const interviews = await Interview.find({ user: req.user._id }).sort({ createdAt: -1 });
  res.json({ interviews });
});

app.get('/api/interviews/:id', auth, async (req, res) => {
  const interview = await Interview.findOne({ _id: req.params.id, user: req.user._id });
  if (!interview) {
    return res.status(404).json({ message: 'Interview not found.' });
  }
  res.json({ interview });
});

app.delete('/api/interviews/:id', auth, async (req, res) => {
  const interview = await Interview.findOneAndDelete({ _id: req.params.id, user: req.user._id });
  if (!interview) {
    return res.status(404).json({ message: 'Interview not found.' });
  }
  res.json({ message: 'Interview deleted.' });
});

app.post('/api/resume/analyze', auth, async (req, res) => {
  try {
    const { summary = '', jobTitle = '', skills = [] } = req.body;
    const analysis = generateResumeAnalysis(summary, jobTitle, skills);
    const created = await ResumeAnalysis.create({
      user: req.user._id,
      score: analysis.score,
      jobTitle,
      summary,
      strengths: analysis.strengths,
      weaknesses: analysis.weaknesses,
      suggestions: analysis.suggestions,
      categories: analysis.categories,
    });

    const progress = await Progress.findOne({ user: req.user._id });
    if (progress) {
      progress.resumeScore = analysis.score;
      await progress.save();
    }

    res.status(201).json({ message: 'Resume analyzed successfully.', analysis: created });
  } catch (error) {
    res.status(500).json({ message: error.message || 'Resume analysis failed.' });
  }
});

app.get('/api/resume/history', auth, async (req, res) => {
  const analyses = await ResumeAnalysis.find({ user: req.user._id }).sort({ createdAt: -1 });
  res.json({ analyses });
});

app.get('/api/questions', async (req, res) => {
  const { type, difficulty, search } = req.query;
  const filter = {};

  if (type && type !== 'All') filter.type = type;
  if (difficulty && difficulty !== 'All') filter.difficulty = difficulty;
  if (search) filter.question = { $regex: search, $options: 'i' };

  const questions = await Question.find(filter).sort({ createdAt: -1 });
  res.json({ questions });
});

app.get('/api/questions/:id', async (req, res) => {
  const question = await Question.findById(req.params.id);
  if (!question) {
    return res.status(404).json({ message: 'Question not found.' });
  }
  res.json({ question });
});

app.post('/api/questions', auth, adminOnly, async (req, res) => {
  const { question, type, category, difficulty, answer } = req.body;
  if (!question) {
    return res.status(400).json({ message: 'Question text is required.' });
  }

  const created = await Question.create({ question, type, category, difficulty, answer });
  res.status(201).json({ message: 'Question created.', question: created });
});

app.put('/api/questions/:id', auth, adminOnly, async (req, res) => {
  const { question, type, category, difficulty, answer } = req.body;
  const updated = await Question.findByIdAndUpdate(
    req.params.id,
    { question, type, category, difficulty, answer },
    { new: true },
  );

  if (!updated) {
    return res.status(404).json({ message: 'Question not found.' });
  }

  res.json({ message: 'Question updated.', question: updated });
});

app.delete('/api/questions/:id', auth, adminOnly, async (req, res) => {
  const deleted = await Question.findByIdAndDelete(req.params.id);
  if (!deleted) {
    return res.status(404).json({ message: 'Question not found.' });
  }
  res.json({ message: 'Question deleted.' });
});

app.get('/api/coding/problems', auth, (req, res) => {
  res.json({ problems: codingProblems });
});

app.post('/api/coding/submit', auth, async (req, res) => {
  const { problemId, language = 'JavaScript', code = '', solved = false } = req.body;
  const problem = codingProblems.find((item) => item._id === problemId) || codingProblems[0];

  const score = solved ? 92 : 68;
  const submission = await CodingSubmission.create({
    user: req.user._id,
    problemId: problem._id,
    title: problem.title,
    language,
    difficulty: problem.difficulty,
    code,
    attempted: true,
    solved,
    score,
  });

  const progress = await Progress.findOne({ user: req.user._id });
  if (progress) {
    progress.codingSolved = (progress.codingSolved || 0) + (solved ? 1 : 0);
    await progress.save();
  }

  res.status(201).json({ message: 'Coding submission recorded.', submission, result: { score, solved } });
});

app.get('/api/progress', auth, async (req, res) => {
  const progress = await buildProgress(req.user._id);
  res.json({ progress });
});

app.get('/api/admin/users', auth, adminOnly, async (req, res) => {
  const users = await User.find().select('-password').sort({ createdAt: -1 });
  res.json({ users });
});

app.put('/api/admin/users/:id/role', auth, adminOnly, async (req, res) => {
  const { role } = req.body;
  if (!['user', 'admin'].includes(role)) {
    return res.status(400).json({ message: 'Role must be user or admin.' });
  }

  const user = await User.findByIdAndUpdate(req.params.id, { role }, { new: true }).select('-password');
  if (!user) {
    return res.status(404).json({ message: 'User not found.' });
  }

  res.json({ message: 'Role updated.', user });
});

app.delete('/api/admin/users/:id', auth, adminOnly, async (req, res) => {
  const user = await User.findByIdAndDelete(req.params.id);
  if (!user) {
    return res.status(404).json({ message: 'User not found.' });
  }
  res.json({ message: 'User deleted.' });
});

app.get('/api/admin/statistics', auth, adminOnly, async (req, res) => {
  const totalUsers = await User.countDocuments();
  const totalQuestions = await Question.countDocuments();
  const totalInterviews = await Interview.countDocuments();
  const averageScore = await Interview.aggregate([{ $group: { _id: null, avg: { $avg: '$score' } } }]);

  res.json({
    stats: {
      totalUsers,
      totalQuestions,
      totalInterviews,
      averageScore: averageScore[0]?.avg ? Math.round(averageScore[0].avg) : 0,
      activeUsers: totalUsers,
    },
  });
});

mongoose
  .connect(process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/AIJobTrainer')
  .then(async () => {
    console.log('MongoDB connected successfully!');
    await seedAdmin();
    await seedQuestions();
    app.listen(PORT, () => {
      console.log(`Backend running on http://localhost:${PORT}`);
    });
  })
  .catch((error) => {
    console.error('MongoDB connection failed:', error.message);
    app.listen(PORT, () => {
      console.log(`Backend running in fallback mode on http://localhost:${PORT}`);
    });
  });

app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ message: 'Something went wrong on the server.' });
});