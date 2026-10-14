package com.aijobtrainer.config;

import com.aijobtrainer.model.CodingProblem;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.model.User;
import com.aijobtrainer.repository.CodingProblemRepository;
import com.aijobtrainer.repository.QuestionRepository;
import com.aijobtrainer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final CodingProblemRepository codingProblemRepository;
    private final PasswordEncoder passwordEncoder;

        @Value("${app.admin.email:}")
        private String adminEmail;

        @Value("${app.admin.password:}")
        private String adminPassword;

    public DataInitializer(UserRepository userRepository,
                           QuestionRepository questionRepository,
                           CodingProblemRepository codingProblemRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.codingProblemRepository = codingProblemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedQuestions();
        seedCodingProblems();
    }

    private void seedAdminUser() {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("Admin user seeding skipped; set ADMIN_EMAIL and ADMIN_PASSWORD to enable it");
            return;
        }
        if (userRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setCollege("AI Institute of Technology");
            admin.setDegree("B.Tech");
            admin.setBranch("Computer Science & Engineering");
            admin.setGraduationYear(2026);
            admin.setRole("ADMIN");
            admin.setSkills(List.of("Java", "Spring Boot", "React", "MongoDB", "System Design"));
            admin.setCreatedAt(Instant.now());
            admin.setUpdatedAt(Instant.now());
            userRepository.save(admin);
            log.info("Admin user seeded: {}", adminEmail);
        }
    }

    private void seedQuestions() {
        if (questionRepository.count() == 0) {
            List<Question> questions = List.of(
                    new Question(
                            "What is the difference between let, const, and var in JavaScript?",
                            "What is the difference between let, const, and var in JavaScript?",
                            "Explain variable scoping, hoisting, and reassignability in modern JavaScript ES6+.",
                            "Technical",
                            "Easy",
                            "Technical",
                            "var is function-scoped and hoisted with undefined. let and const are block-scoped; const cannot be reassigned."
                    ),
                    new Question(
                            "Explain the architectural components of Spring Boot.",
                            "Explain the architectural components of Spring Boot.",
                            "Detail dependency injection, auto-configuration, starter POMs, and the Spring IoC container.",
                            "Technical",
                            "Medium",
                            "Technical",
                            "Spring Boot provides auto-configuration, embedded Tomcat server, opinionated starters, and IoC container for bean lifecycle."
                    ),
                    new Question(
                            "Tell me about a difficult challenge you resolved using the STAR method.",
                            "Tell me about a difficult challenge you resolved using the STAR method.",
                            "Provide Situation, Task, Action, and Measurable Result of a past project hurdle.",
                            "HR",
                            "Medium",
                            "HR",
                            "Situation: Production database bottle-neck; Task: Reduce latency; Action: Indexed collections and normalized queries; Result: 60% faster queries."
                    ),
                    new Question(
                            "What are the differences between SQL and NoSQL (MongoDB)?",
                            "What are the differences between SQL and NoSQL (MongoDB)?",
                            "Compare ACID properties, relational schema vs flexible JSON/BSON documents, and horizontal scalability.",
                            "SQL",
                            "Medium",
                            "SQL",
                            "SQL databases are relational and structured with strict schemas; MongoDB is document-based, highly flexible, and scales horizontally."
                    ),
                    new Question(
                            "Explain the difference between Optimistic and Pessimistic Locking.",
                            "Explain the difference between Optimistic and Pessimistic Locking.",
                            "Discuss concurrency control in databases, version numbers, and record locks.",
                            "Technical",
                            "Hard",
                            "Technical",
                            "Optimistic locking assumes collisions are rare and verifies version tags before commit. Pessimistic locking acquires exclusive locks on records."
                    ),
                    new Question(
                            "How do you implement binary search and what is its time complexity?",
                            "How do you implement binary search and what is its time complexity?",
                            "Discuss preconditions (sorted array), logarithmic time O(log n), and boundary pointer manipulation.",
                            "DSA",
                            "Easy",
                            "DSA",
                            "Binary search repeatedly divides the search interval in half. Time complexity is O(log n)."
                    ),
                    new Question(
                            "How do you handle disagreement with a tech lead or senior developer?",
                            "How do you handle disagreement with a tech lead or senior developer?",
                            "Focus on active listening, data-driven technical reasoning, respectful compromise, and team alignment.",
                            "Behavioral",
                            "Medium",
                            "Behavioral",
                            "Listen actively, understand the architectural trade-offs, present benchmarks respectfully, and commit to the agreed direction."
                    ),
                    new Question(
                            "What is the difference between INNER JOIN, LEFT JOIN, and FULL OUTER JOIN?",
                            "What is the difference between INNER JOIN, LEFT JOIN, and FULL OUTER JOIN?",
                            "Explain row matching conditions and NULL propagation in relational queries.",
                            "SQL",
                            "Easy",
                            "SQL",
                            "INNER JOIN returns matching rows only; LEFT JOIN returns all rows from left plus matches; FULL OUTER JOIN returns rows when there is a match in either."
                    )
            );
            questionRepository.saveAll(questions);
            log.info("Default question bank seeded: {} questions.", questions.size());
        }
    }

    private void seedCodingProblems() {
        if (codingProblemRepository.count() == 0) {
            List<CodingProblem> problems = List.of(
                    new CodingProblem(
                            "prob-1",
                            "Two Sum",
                            "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution, and you may not use the same element twice.",
                            "DSA",
                            "EASY",
                            "nums = [2, 7, 11, 15], target = 9",
                            "[0, 1]",
                            List.of("Input: nums = [2,7,11,15], target = 9 -> Output: [0,1] because nums[0] + nums[1] == 9"),
                            "2 <= nums.length <= 10^4, -10^9 <= nums[i] <= 10^9, Only one valid answer exists.",
                            List.of("Array", "Hash Table")
                    ),
                    new CodingProblem(
                            "prob-2",
                            "Palindrome Check",
                            "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.",
                            "JavaScript",
                            "EASY",
                            "s = \"A man, a plan, a canal: Panama\"",
                            "true",
                            List.of("Input: \"A man, a plan, a canal: Panama\" -> Output: true", "Input: \"race a car\" -> Output: false"),
                            "1 <= s.length <= 2 * 10^5. s consists only of printable ASCII characters.",
                            List.of("Two Pointers", "String")
                    ),
                    new CodingProblem(
                            "prob-3",
                            "Longest Substring Without Repeating Characters",
                            "Given a string s, find the length of the longest substring without repeating characters.",
                            "Python",
                            "MEDIUM",
                            "s = \"abcabcbb\"",
                            "3",
                            List.of("Input: s = \"abcabcbb\" -> Output: 3 (The answer is \"abc\", with the length of 3.)"),
                            "0 <= s.length <= 5 * 10^4. s consists of English letters, digits, symbols and spaces.",
                            List.of("Hash Table", "String", "Sliding Window")
                    ),
                    new CodingProblem(
                            "prob-4",
                            "Valid Parentheses",
                            "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. Open brackets must be closed by the same type of brackets in the correct order.",
                            "DSA",
                            "EASY",
                            "s = \"()[]{}\"",
                            "true",
                            List.of("Input: s = \"()[]{}\" -> Output: true", "Input: s = \"(]\" -> Output: false"),
                            "1 <= s.length <= 10^4. s consists of parentheses only '()[]{}'.",
                            List.of("Stack", "String")
                    ),
                    new CodingProblem(
                            "prob-5",
                            "Highest Salary Department Query",
                            "Write an SQL query to find employees who have the highest salary in each of the departments.",
                            "SQL",
                            "MEDIUM",
                            "Employee(id, name, salary, departmentId), Department(id, name)",
                            "Department, Employee, Salary",
                            List.of("SELECT d.name AS Department, e.name AS Employee, e.salary FROM Employee e JOIN Department d ON e.departmentId = d.id WHERE (e.departmentId, e.salary) IN (SELECT departmentId, MAX(salary) FROM Employee GROUP BY departmentId)"),
                            "Standard ANSI SQL compliant query.",
                            List.of("SQL", "Database", "Subquery")
                    )
            );
            codingProblemRepository.saveAll(problems);
            log.info("Default coding problems seeded: {} problems.", problems.size());
        }
    }
}
