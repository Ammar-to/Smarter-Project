package com.example.capstoneproject.Request;

import com.example.capstoneproject.courses.Course;
import com.example.capstoneproject.session.MySession;
import com.example.capstoneproject.session.SessionService;
import com.example.capstoneproject.student.Student;
import com.example.capstoneproject.student.StudentRepository;
import com.example.capstoneproject.tutor.Tutor;
import com.example.capstoneproject.tutor.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final SessionService sessionService;

    public List getRequests(){
        return requestRepository.findAll();
    }
    public void sendRequest(MyRequest request) throws Exception {
        Tutor tutor = tutorRepository.findById(request.getTutorId()).get();
        Student student =studentRepository.findById(request.getStudentId()).get();
        if(tutor == null){
            throw new Exception();
            }
        for(Course course: tutor.getCourses()){
            if (course.getName().equalsIgnoreCase(request.getCourseName())){
                tutor.getRequests().add(request);
                student.getRequests().add(request);
                requestRepository.save(request);
                studentRepository.save(student);
                tutorRepository.save(tutor);
                break;
            }else
                throw new Exception();


        }
    }


    public void removeAllRequests() {
        requestRepository.deleteAll();
    }

    public void acceptRequest(Integer requestId, Integer tutorId, MySession session) throws Exception{
        MyRequest request = requestRepository.findById(requestId).get();
        if(request == null || request.getTutorId() != tutorId) throw new Exception();
        request.setStatus("Accepted");
        requestRepository.save(request);
        sessionService.createSession(session);
        createSessionWithTutorId(session,tutorId);
        addSessionToStudent(request.getStudentId(), session.getSessionId());
    }
    public void createSessionWithTutorId(MySession session, Integer id) throws Exception {
        Tutor t = tutorRepository.findById(id).get();
        if(t != null){
            t.getMySessions().add(session);
            tutorRepository.save(t);
        }else
            throw new Exception("Tutor not found");

    }

    public void addSessionToStudent(Integer studentId, Integer sessionId) throws Exception {
        Student student = studentRepository.findById(studentId).get();
        MySession session = sessionService.findSession(sessionId);
        if(student != null){
            if(session != null){
                student.getStudentSessions().add(session);
                studentRepository.save(student);
            } else
                throw new Exception("Student Cannot be null");
        }else
            throw new Exception("Student Cannot be null");

    }
}
