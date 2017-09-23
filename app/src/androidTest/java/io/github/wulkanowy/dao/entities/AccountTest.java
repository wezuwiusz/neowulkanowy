package io.github.wulkanowy.dao.entities;

import android.support.test.filters.SmallTest;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

@SmallTest
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
