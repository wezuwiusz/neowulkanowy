package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class AccountTest extends AbstractDaoTestLongPk<AccountDao, Account> {

    public AccountTest() {
        super(AccountDao.class);
    }

    @Override
    protected Account createEntity(Long key) {
        Account entity = new Account();
        entity.setId(key);
        return entity;
    }

}
