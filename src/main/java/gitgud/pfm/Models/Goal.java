package gitgud.pfm.Models;

import gitgud.pfm.utils.IdGenerator;

public class Goal extends FinancialEntity {
    private double target;
    private double priority;
    private String createTime;
    private String deadline;
    private String walletId; // link goal to specific wallet for funding
    
    // No-arg constructor required for reflection-based mapping (do not auto-persist)
    public Goal() {
        super(null, null, 0.0);
    }
    
    public Goal(String name, double target, double current, 
                String deadline, double priority, String createTime) {
        super(IdGenerator.generateGoalId(), name, current);
        this.target = target;
        this.deadline = deadline;
        this.priority = priority;
        this.createTime = createTime;
        this.walletId = null; // Default: not linked to specific wallet
    }
    
    public Goal(String name, double target, double current, 
                String deadline, double priority, String createTime, String walletId) {
        super(IdGenerator.generateGoalId(), name, current);
        this.target = target;
        this.deadline = deadline;
        this.priority = priority;
        this.createTime = createTime;
        this.walletId = walletId;
    }
    
    public double getTarget() { return target; }
    public void setTarget(double target) { this.target = target; }
    
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    
    public double getPriority() { return priority; }
    public void setPriority(double priority) { this.priority = priority; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }
}
