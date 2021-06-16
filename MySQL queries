-- Milestone 2: 10 Queries and a Stored Procedure
-- Created by Baran Onalan, Casey Nguyen and Tran Bui
-- Finalized March 13, 2021
USE Group5Collab;

-- #1 Show the average exam grade.
SELECT AVG(ExamGrade)
FROM Exam;

-- #2 List all of the instructors that work in "A" building, sorted by room number.
SELECT DISTINCT InstructorLName, InstructorFName
FROM Section JOIN Instructor USING(InstructorID)
WHERE RoomNumber LIKE 'A%'
ORDER BY RoomNumber;

-- #3 List all class times in room A202.
SELECT RoomNumber, SectionDays, SectionTime
FROM Section
WHERE RoomNumber = "A202"
ORDER BY SectionTime DESC;

-- #4 Find the contact information for instructors with the last name "Lidgate".
SELECT InstructorLName, InstructorFName, InstructorEmail, InstructorPhone
FROM Instructor
WHERE InstructorLNAME = "Durnall";

-- #5 List the courses offered in the Winter quarter.
SELECT QuarterDesc, CourseName
FROM Quarter JOIN Section USING(QuarterID) JOIN Course USING(CourseID)
WHERE QuarterDesc = "Winter";

-- #6 Return the number of students that have graduated before 2020.
SELECT COUNT(StudentID)
FROM Student
WHERE StudentGradYear < 2020;

-- #7 Return the name of students that were in the group named "Gorilla gorilla".
SELECT CONCAT(StudentFNAME, ' ' ,StudentLNAME) StudentFULLNAME
FROM Student JOIN Record USING(RecordID) JOIN Assignment USING (AssignmentID) JOIN Project USING (ProjectID)
WHERE GroupName = 'Gorilla gorilla'
ORDER BY StudentFULLNAME;

-- #8 List the exam history of the student with the ID '41908376'.
SELECT RecordID, AssignmentID, ExamID, ExamGrade
FROM Student JOIN Record USING(RecordID) JOIN Assignment USING(AssignmentID) JOIN Exam USING(ExamID)
WHERE StudentID = 41908376;

-- #9 Show the class schedule for student(s) named "Tilly Lepope".
SELECT StudentID, SectionID, CourseName, CourseCredit, SectionDays, SectionTime, RoomNumber
FROM Student JOIN SectionStudent USING(StudentID) JOIN Section USING(SectionID) JOIN Course USING(CourseID)
WHERE StudentFName = "Tilly" AND StudentLName = "Lepope";

-- #10 Show the names of all students in the secton with ID '17009066', ordered by last name.
SELECT SectionID, StudentID, StudentLName, StudentFName
FROM Section JOIN SectionStudent USING(SectionID) JOIN Student USING(StudentID)
WHERE SECTIONID = 17009066
ORDER BY StudentLName;

-- #11 Update first name with ID = 61749685.
UPDATE Student 
SET StudentFName = 'Nathan', StudentGradYear = 2011
WHERE StudentID = 61749685;

-- #12 Show the fullnames of the graduated students with AVG(ExamGrade) >= 90, order by name.  
SELECT CONCAT(StudentFNAME, ' ' ,StudentLNAME) StudentFullName, RecordID, AssignmentID, ExamID, ROUND(AVG(ExamGrade),2) AS AVG_ExamGrade
FROM Student JOIN Record USING(RecordID) JOIN Assignment USING(AssignmentID) 
JOIN Exam USING (ExamID) JOIN Quarter USING (QuarterID)
WHERE StudentGradYear <= 2021
GROUP BY CONCAT(StudentFNAME, ' ' ,StudentLNAME), RecordID, AssignmentID, ExamID
HAVING AVG_ExamGrade >= 90
ORDER BY StudentFullName;

-- STORED PROCEDURE
DELIMITER //
CREATE PROCEDURE FindMySchedule(IN firstName VARCHAR(45), lastName VARCHAR(45))

BEGIN
	SELECT StudentID, SectionID, CourseName, CourseCredit, SectionDays, SectionTime, RoomNumber
    FROM Student JOIN SectionStudent USING(StudentID) JOIN Section USING(SectionID) JOIN Course USING(CourseID)
    WHERE StudentFName LIKE firstName AND StudentLName LIKE lastName;
END //
DELIMITER ;

CALL FindMySchedule('Avis', 'Denziloe');
