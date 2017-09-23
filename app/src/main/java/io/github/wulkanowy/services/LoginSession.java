package io.github.wulkanowy.services;


import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.dao.entities.DaoSession;

public class LoginSession {

    private Long userId;

    private Vulcan vulcan;

    private DaoSession daoSession;

    public Long getUserId() {
        return userId;
    }

    public LoginSession setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Vulcan getVulcan() {
        return vulcan;
    }

    public LoginSession setVulcan(Vulcan vulcan) {
        this.vulcan = vulcan;
        return this;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public LoginSession setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        return this;
    }
}
