package com.sotomaque.ppmtool.repositories;

import com.sotomaque.ppmtool.domain.Backlog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacklogRepository  extends CrudRepository<Backlog, Long> {

    Backlog findByProjectIdentifier(String Identifier);
}
