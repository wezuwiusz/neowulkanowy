package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import io.github.wulkanowy.data.db.dao.entities.Symbol;
import io.github.wulkanowy.data.db.dao.entities.SymbolDao;

public class SymbolTest extends AbstractDaoTestLongPk<SymbolDao, Symbol> {

    public SymbolTest() {
        super(SymbolDao.class);
    }

    @Override
    protected Symbol createEntity(Long key) {
        Symbol entity = new Symbol();
        entity.setId(key);
        return entity;
    }

}
