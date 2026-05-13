package com.scouting.playerservice.infrastructure;

import com.scouting.playerservice.application.port.PlayerJdbcQueryPort;
import com.scouting.playerservice.domain.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JdbcTemplatePlayerQueryAdapter implements PlayerJdbcQueryPort {

    private static final RowMapper<Player> PLAYER_ROW_MAPPER = (rs, rowNum) -> new Player(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getString("position"),
            rs.getInt("age"),
            rs.getString("club"),
            rs.getString("preferred_foot")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePlayerQueryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Player> findAllPlayers() {
        return jdbcTemplate.query(
                "select id, name, position, age, coalesce(club, '') as club, coalesce(preferred_foot, 'Right') as preferred_foot from players order by name asc",
                PLAYER_ROW_MAPPER
        );
    }
}
