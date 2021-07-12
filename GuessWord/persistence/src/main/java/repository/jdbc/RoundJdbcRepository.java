package repository.jdbc;

import domain.Round;
import domain.Word;
import domain.validators.RoundValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import repository.RoundRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoundJdbcRepository implements RoundRepository {

    private RoundValidator roundValidator;
    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public RoundJdbcRepository(Properties properties, RoundValidator roundValidator) {
        this.jdbcUtils = new JdbcUtils(properties);
        this.roundValidator = roundValidator;
    }

    @Override
    public Round findOne(Integer id) {
        logger.traceEntry("Finding task with id {} ", id);
        Connection connection = jdbcUtils.getConnection();
        Round round = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rounds WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String word = resultSet.getString("word");
                    Integer points = resultSet.getInt("points");

                    round = new Round(username, word, points);
                    round.setId(id);

                    return round;
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit("No task found with id {}", id);
        return round;
    }

    @Override
    public Iterable<Round> findAll() {
        logger.traceEntry();
        Connection connection = jdbcUtils.getConnection();

        List<Round> rounds = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rounds")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String word = resultSet.getString("word");
                    Integer points = resultSet.getInt("points");

                    Round round = new Round(username, word, points);
                    round.setId(id);

                    rounds.add(round);
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit(rounds);
        return rounds;
    }

    @Override
    public void save(Round entity) {
        logger.traceEntry("Saving task {}", entity);
        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Rounds VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setString(2, entity.getUsername());
            preparedStatement.setString(3, entity.getWord());
            preparedStatement.setInt(4, entity.getPoints());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("Deleting task with {}", id);
        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Rounds WHERE id = ?")){
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException){
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit();
    }

    @Override
    public void update(Round entity) {
        logger.traceEntry("Updating task {}", entity);
        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Rounds SET username = ?, word = ?, points = ? WHERE id = ?")) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getWord());
            preparedStatement.setInt(3, entity.getPoints());
            preparedStatement.setInt(4, entity.getId());
            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit();
    }

    @Override
    public Round findWordByUsername(String username) {
        logger.traceEntry("Finding task with username and password {} ", username);
        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Rounds WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                    String word = resultSet.getString("word");
                    Integer points = resultSet.getInt("points");
                    Integer id = resultSet.getInt("id");

                    Round round = new Round(username, word, points);
                    round.setId(id);

                    return round;
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException);
            sqlException.printStackTrace();
        }
        logger.traceExit("No task found with username {}, ", username);
        return null;
    }
}
