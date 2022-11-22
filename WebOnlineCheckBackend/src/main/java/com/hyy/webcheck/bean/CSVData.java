package com.hyy.webcheck.bean;

import com.hyy.webcheck.bean.User.User;
import lombok.Data;

import java.util.List;

@Data
public class CSVData {

    public List<Answer> answers;

    public List<User> users;
}
