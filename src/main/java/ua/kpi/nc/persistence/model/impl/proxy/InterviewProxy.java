package ua.kpi.nc.persistence.model.impl.proxy;

import ua.kpi.nc.persistence.model.*;
import ua.kpi.nc.persistence.model.impl.real.InterviewImpl;
import ua.kpi.nc.service.InterviewService;
import ua.kpi.nc.service.ServiceFactory;

import java.sql.Timestamp;
import java.util.List;

public class InterviewProxy implements Interview {

	private static final long serialVersionUID = 1092260058342461849L;

	private Long id;

	private InterviewImpl interview;

	private InterviewService interviewService;
	private List<FormAnswer> answers;

	public InterviewProxy() {
	}

	public InterviewProxy(Long id) {
		super();
		this.id = id;
	}

	@Override
	public List<FormAnswer> getAnswers() {
		return answers;
	}

	@Override
	public void setAnswers(List<FormAnswer> answers) {
		this.answers = answers;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMark() {
		checkInterview();
		return interview.getMark();
	}

	public void setMark(Integer mark) {
		checkInterview();
		interview.setMark(mark);
	}

	public Timestamp getDate() {
		checkInterview();
		return interview.getDate();
	}

	public void setDate(Timestamp date) {
		checkInterview();
		interview.setDate(date);
	}

	public User getInterviewer() {
		checkInterview();
		return interview.getInterviewer();
	}

	public void setInterviewer(User user) {
		checkInterview();
		interview.setInterviewer(user);
	}

	public Role getRole() {
		checkInterview();
		return interview.getRole();
	}

	public void setRole(Role role) {
		checkInterview();
		interview.setRole(role);
	}

	public Boolean isAdequateMark() {
		checkInterview();
		return interview.isAdequateMark();
	}

	public void setAdequateMark(Boolean adequateMark) {
		checkInterview();
		interview.setAdequateMark(adequateMark);
	}

	public ApplicationForm getApplicationForm() {
		checkInterview();
		return interview.getApplicationForm();
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		checkInterview();
		interview.setApplicationForm(applicationForm);
	}
	private void checkInterview(){
		if (interview == null) {
			interviewService = ServiceFactory.getInterviewService();
			interview = downloadInterview();
		}
	}

	private InterviewImpl downloadInterview() {
		return (InterviewImpl) interviewService.getById(id);
	}

}
