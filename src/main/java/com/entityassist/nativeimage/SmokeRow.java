package com.entityassist.nativeimage;

import com.entityassist.BaseEntity;
import com.entityassist.querybuilder.QueryBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "entityassist_native_smoke")
public class SmokeRow extends BaseEntity<SmokeRow, SmokeRow.Builder, String>
{
    @Id
    private String id;
    private String label;

    public SmokeRow()
    {
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public SmokeRow setId(String id)
    {
        this.id = id;
        return this;
    }

    String label()
    {
        return label;
    }

    public static final class Builder extends QueryBuilder<Builder, SmokeRow, String>
    {
        private final CriteriaBuilder criteriaBuilder;

        Builder(CriteriaBuilder criteriaBuilder)
        {
            this.criteriaBuilder = criteriaBuilder;
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder()
        {
            return criteriaBuilder;
        }

        @Override
        public boolean isIdGenerated()
        {
            return false;
        }
    }
}
