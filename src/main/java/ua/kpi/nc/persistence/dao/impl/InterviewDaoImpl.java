package ua.kpi.nc.persistence.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.kpi.nc.persistence.dao.InterviewDao;
import ua.kpi.nc.persistence.model.ApplicationForm;
import ua.kpi.nc.persistence.model.Interview;
import ua.kpi.nc.persistence.model.Role;
import ua.kpi.nc.persistence.model.User;
import ua.kpi.nc.persistence.model.impl.proxy.ApplicationFormProxy;
import ua.kpi.nc.persistence.model.impl.proxy.UserProxy;
import ua.kpi.nc.persistence.model.impl.real.InterviewImpl;
import ua.kpi.nc.persistence.util.JdbcTemplate;
import ua.kpi.nc.persistence.util.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by Nikita on 23.04.2016.
 */
public class InterviewDaoImpl extends JdbcDaoSupport implements InterviewDao {
    private static final String SQL_GET_BY_ID =
            "SELECT i.id, i.mark, i.date, i.id_interviewer, i.interviewer_role, i.adequate_mark, i.id_application_form \n" +
                    "FROM interview i \n" +
                    "WHERE i.id = ?;";
    private static final String SQL_GET_BY_INTERVIEWER =
            "SELECT i.id, i.mark, i.date, i.id_interviewer, i.interviewer_role, i.adequate_mark, i.id_application_form \n" +
                    "FROM interview i \n" +
                    "WHERE i.id_interviewer = ?;";
    private static final String SQL_GET_BY_APPLICATION_FORM =
            "SELECT i.id, i.mark, i.date, i.id_interviewer, i.interviewer_role, i.adequate_mark, i.id_application_form \n" +
                    "FROM interview i \n" +
                    "WHERE i.id_application_form = ?;";
    private static final String SQL_INSERT = "INSERT INTO interview( mark, date, id_interviewer, " +
            " interviewer_role, adequate_mark, id_application_form) " +
            "VALUES (?, ?, ?, ?, ?, ?);";
    private static final String SQL_DELETE = "DELETE FROM interview WHERE interview.id = ?;";
    private static final String SQL_UPDATE = "UPDATE interview SET mark = ?, date= ?,"
            + " id_interviewer = ?, interviewer_role= ?, adequate_mark= ?, id_application_form= ? WHERE id = ?";

    private static Logger log = LoggerFactory.getLogger(InterviewDaoImpl.class.getName());

    public InterviewDaoImpl(DataSource dataSource) {
        this.setJdbcTemplate(new JdbcTemplate(dataSource));
    }

    @Override
    public Interview getById(Long id) {
        if (log.isInfoEnabled()) {
            log.info("Looking for interview with id = " + id);
        }
        return this.getJdbcTemplate().queryWithParameters(SQL_GET_BY_ID, new InterviewExtractor(), id);

    }

    @Override
    public Set<Interview> getByInterviewer(User user) {
        if (log.isInfoEnabled()) {
            log.info("Looking for interview with interviewer = " + user.getFirstName() + " " + user.getLastName());
        }
        return this.getJdbcTemplate().queryForSet(SQL_GET_BY_INTERVIEWER, new InterviewExtractor(), user.getId());
    }

    @Override
    public Set<Interview> getByApplicationForm(ApplicationForm applicationForm) {
        if (log.isInfoEnabled()) {
            log.info("Looking for interview with application form id = " + applicationForm.getId());
        }
        return this.getJdbcTemplate().queryForSet(SQL_GET_BY_APPLICATION_FORM, new InterviewExtractor(), applicationForm.getId());

    }

    @Override
    public Long insertInterview(Interview interview, ApplicationForm applicationForm, User interviewer, Role role) {

        if (log.isInfoEnabled()) {
            log.info("Insert interview with id = " + interview.getId());
        }
        return this.getJdbcTemplate().insert(SQL_INSERT, interview.getMark(), interview.getDate(), interview.getId(),
                role.getId(), interview.isAdequateMark(), applicationForm.getId());
    }

    @Override
    public Long insertInterview(Interview interview, ApplicationForm applicationForm, User interviewer, Role role, Connection connection) {
        if (log.isInfoEnabled()) {
            log.info("Insert interview with id = " + interview.getId());
        }
        return this.getJdbcTemplate().insert(SQL_INSERT,connection, interview.getMark(), interview.getDate(), interview.getId(),
                role.getId(), interview.isAdequateMark(), applicationForm.getId());
    }

    @Override
    public int updateInterview(Interview interview) {
        if (log.isInfoEnabled()) {
            log.info("Update interview with id = " + interview.getId());
        }
        return this.getJdbcTemplate().update(SQL_UPDATE, interview.getId());
    }

    @Override
    public int deleteInterview(Interview interview) {

        if (log.isInfoEnabled()) {
            log.info("Delete interview with id = " + interview.getId());
        }
        return this.getJdbcTemplate().update(SQL_DELETE, interview.getId());
    }

    @Override
    public Set<Interview> getAll() {
        return null;
    }

    private final class InterviewExtractor implements ResultSetExtractor<Interview> {
        @Override
        public Interview extractData(ResultSet resultSet) throws SQLException {
            Interview interview = new InterviewImpl();
            interview.setId(resultSet.getLong("id"));
            interview.setAdequateMark(resultSet.getBoolean("adequate_mark"));
            interview.setDate(resultSet.getTimestamp("date"));
            interview.setMark(resultSet.getInt("mark"));
            interview.setApplicationForm(new ApplicationFormProxy(resultSet.getLong("id_application_form")));
            interview.setInterviewer(new UserProxy(resultSet.getLong("id_interviewer")));
            //interview.setRole(new RoleProxy(resultSet.getLong("id_role")));
            return interview;
        }
    }
}