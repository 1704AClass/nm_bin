package com.ningmeng.manage_course.service;

import com.ningmeng.framework.domain.system.SysDictionary;
import com.ningmeng.manage_course.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangb on 2020/2/19.
 */
@Service
public class SysDicthinaryService {

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;
    public SysDictionary getByType(String type) {
        SysDictionary sysDictionary =  sysDictionaryRepository.findBydType(type);
        if(sysDictionary==null){
            return null;
        }
        return sysDictionary;
    }
}
