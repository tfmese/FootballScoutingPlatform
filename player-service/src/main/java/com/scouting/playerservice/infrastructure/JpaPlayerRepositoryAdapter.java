package com.scouting.playerservice.infrastructure;

import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPlayerRepositoryAdapter implements PlayerRepository {

    private final SpringDataPlayerRepository repository;

    public JpaPlayerRepositoryAdapter(SpringDataPlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Player save(Player player) {
        return repository.save(player);
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Player> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
