package io.github.wulkanowy.services;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LoginSession that = (LoginSession) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(vulcan, that.vulcan)
                .append(daoSession, that.daoSession)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(vulcan)
                .append(daoSession)
                .toHashCode();
    }
}
