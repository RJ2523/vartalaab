package com.chatapp.vartalaab.repository;

import com.chatapp.vartalaab.redisEntity.OfflineMessageIds;
import org.springframework.data.repository.CrudRepository;

public interface OfflineMessageIdsRepository extends CrudRepository<OfflineMessageIds, String> {

}
