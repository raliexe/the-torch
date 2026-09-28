package com.torchteam.thetorch.repository;

import com.torchteam.thetorch.model.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TreeRepository extends JpaRepository<Tree, UUID> {}