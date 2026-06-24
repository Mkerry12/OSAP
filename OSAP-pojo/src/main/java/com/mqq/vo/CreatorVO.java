package com.mqq.vo;

import com.mqq.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorVO implements Serializable {

    private Long id;
    private String username;

    public static CreatorVO from(User user) {
        if (user == null) return null;
        return new CreatorVO(user.getId(), user.getUsername());
    }
}
