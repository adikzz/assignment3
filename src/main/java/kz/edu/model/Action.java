package kz.edu.model;

import javax.persistence.*;
import java.security.Timestamp;

@Entity(name = "ActionsEntity")
@Table(name = "actions")
public class Action {
    private int action_id;
    private String action_message;
    private String createTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    public int getAction_id() { return action_id; }
    public void setAction_id(int action_id) { this.action_id = action_id; }

    @Column(name = "action_message")
    public String getAction_message() { return action_message; }
    public void setAction_message(String action_message) { this.action_message = action_message; }

    @Column(name = "createTime")
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
