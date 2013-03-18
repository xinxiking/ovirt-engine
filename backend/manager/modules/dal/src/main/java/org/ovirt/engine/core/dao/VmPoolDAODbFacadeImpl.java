package org.ovirt.engine.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmPool;
import org.ovirt.engine.core.common.businessentities.VmPoolMap;
import org.ovirt.engine.core.common.businessentities.VmPoolType;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.compat.NGuid;
import org.ovirt.engine.core.dao.VmDAODbFacadeImpl.VMRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * <code>VmPoolDAODbFacadeImpl</code> provides an implementation of {@link VmPoolDAO} based on implementation code from
 * {@link org.ovirt.engine.core.dal.dbbroker.DbFacade}.
 *
 */
public class VmPoolDAODbFacadeImpl extends BaseDAODbFacade implements VmPoolDAO {
    @Override
    public void removeVmFromVmPool(Guid id) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_guid", id);

        getCallsHandler().executeModification("DeleteVm_pool_map", parameterSource);
    }

    @Override
    public VmPool get(NGuid id) {
        return get(id, null, false);
    }

    @Override
    public VmPool get(NGuid id, Guid userID, boolean isFiltered) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_pool_id", id).addValue("user_id", userID).addValue("is_filtered", isFiltered);
        return getCallsHandler().executeRead("GetVm_poolsByvm_pool_id", VmPoolFullRowMapper.instance, parameterSource);
    }

    @Override
    public VmPool getByName(String name) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_pool_name", name);
        return getCallsHandler().executeRead("GetVm_poolsByvm_pool_name",
                VmPoolNonFullRowMapper.instance,
                parameterSource);
    }

    @Override
    public List<VmPool> getAll() {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource();
        return getCallsHandler().executeReadList("GetAllFromVm_pools", VmPoolFullRowMapper.instance, parameterSource);
    }

    @Override
    public List<VmPool> getAllForUser(Guid id) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("user_id", id);
        return getCallsHandler().executeReadList("GetAllVm_poolsByUser_id_with_groups_and_UserRoles",
                VmPoolNonFullRowMapper.instance, parameterSource);
    }

    @Override
    public List<VmPool> getAllWithQuery(String query) {
        return new SimpleJdbcTemplate(jdbcTemplate).query(query, VmPoolFullRowMapper.instance);
    }

    @Override
    public void save(VmPool pool) {
        Guid id = pool.getVmPoolId();
        if (Guid.isNullOrEmpty(id)) {
            id = Guid.NewGuid();
            pool.setVmPoolId(id);
        }
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_pool_description", pool.getVmPoolDescription())
                .addValue("vm_pool_id", pool.getVmPoolId())
                .addValue("vm_pool_name", pool.getName())
                .addValue("vm_pool_type", pool.getVmPoolType())
                .addValue("parameters", pool.getParameters())
                .addValue("prestarted_vms", pool.getPrestartedVms())
                .addValue("vds_group_id", pool.getVdsGroupId());

        getCallsHandler().executeModification("InsertVm_pools", parameterSource);
    }

    @Override
    public void update(VmPool pool) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_pool_description", pool.getVmPoolDescription())
                .addValue("vm_pool_id", pool.getVmPoolId())
                .addValue("vm_pool_name", pool.getName())
                .addValue("vm_pool_type", pool.getVmPoolType())
                .addValue("parameters", pool.getParameters())
                .addValue("prestarted_vms", pool.getPrestartedVms())
                .addValue("vds_group_id", pool.getVdsGroupId());

        getCallsHandler().executeModification("UpdateVm_pools", parameterSource);
    }

    @Override
    public void remove(NGuid id) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("vm_pool_id", id);

        getCallsHandler().executeModification("DeleteVm_pools", parameterSource);
    }

    @Override
    public VmPoolMap getVmPoolMapByVmGuid(Guid vmId) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource().addValue("vm_guid", vmId);

        RowMapper<VmPoolMap> mapper = new RowMapper<VmPoolMap>() {
            @Override
            public VmPoolMap mapRow(ResultSet rs, int rowNum) throws SQLException {
                VmPoolMap entity = new VmPoolMap();
                entity.setvm_guid(Guid.createGuidFromString(rs.getString("vm_guid")));
                entity.setvm_pool_id(Guid.createGuidFromString(rs.getString("vm_pool_id")));
                return entity;
            }
        };

        return getCallsHandler().executeRead("GetVm_pool_mapByvm_guid", mapper, parameterSource);
    }

    @Override
    public void addVmToPool(VmPoolMap map) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource().addValue("vm_guid", map.getvm_guid())
                .addValue("vm_pool_id", map.getvm_pool_id());

        getCallsHandler().executeModification("InsertVm_pool_map", parameterSource);
    }

    @Override
    public List<VmPoolMap> getVmPoolsMapByVmPoolId(NGuid vmPoolId) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource().addValue("vm_pool_id", vmPoolId);

        RowMapper<VmPoolMap> mapper = new RowMapper<VmPoolMap>() {
            @Override
            public VmPoolMap mapRow(ResultSet rs, int rowNum) throws SQLException {
                VmPoolMap entity = new VmPoolMap();
                entity.setvm_guid(Guid.createGuidFromString(rs.getString("vm_guid")));
                entity.setvm_pool_id(Guid.createGuidFromString(rs.getString("vm_pool_id")));
                return entity;
            }
        };

        return getCallsHandler().executeReadList("GetVm_pool_mapByvm_pool_id", mapper, parameterSource);
    }

    @Override
    public List<VmPoolMap> getVmMapsInVmPoolByVmPoolIdAndStatus(NGuid vmPoolId, VMStatus vmStatus) {
        MapSqlParameterSource parameterSource =
                getCustomMapSqlParameterSource().addValue("vm_pool_id", vmPoolId).addValue("status",
                        vmStatus.getValue());

        RowMapper<VmPoolMap> mapper = new RowMapper<VmPoolMap>() {
            @Override
            public VmPoolMap mapRow(ResultSet rs, int rowNum) throws SQLException {
                VmPoolMap entity = new VmPoolMap();
                entity.setvm_guid(Guid.createGuidFromString(rs.getString("vm_guid")));
                entity.setvm_pool_id(Guid.createGuidFromString(rs.getString("vm_pool_id")));
                return entity;
            }
        };

        return getCallsHandler().executeReadList("getVmMapsInVmPoolByVmPoolIdAndStatus", mapper,
                parameterSource);
    }

    private static final class VmPoolFullRowMapper implements RowMapper<VmPool> {
        public final static VmPoolFullRowMapper instance = new VmPoolFullRowMapper();

        @Override
        public VmPool mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final VmPool entity = new VmPool();
            entity.setVmPoolDescription(rs
                    .getString("vm_pool_description"));
            entity.setVmPoolId(Guid.createGuidFromString(rs
                    .getString("vm_pool_id")));
            entity.setName(rs.getString("vm_pool_name"));
            entity.setVmPoolType(VmPoolType.forValue(rs
                    .getInt("vm_pool_type")));
            entity.setParameters(rs.getString("parameters"));
            entity.setPrestartedVms(rs.getInt("prestarted_vms"));
            entity.setVdsGroupId(Guid.createGuidFromString(rs
                    .getString("vds_group_id")));
            entity.setVdsGroupName(rs.getString("vds_group_name"));
            entity.setAssignedVmsCount(rs.getInt("assigned_vm_count"));
            entity.setRunningVmsCount(rs.getInt("vm_running_count"));
            return entity;
        }
    }

    private static final class VmPoolNonFullRowMapper implements RowMapper<VmPool> {
        public final static VmPoolNonFullRowMapper instance = new VmPoolNonFullRowMapper();

        @Override
        public VmPool mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final VmPool entity = new VmPool();
            entity.setVmPoolDescription(rs
                    .getString("vm_pool_description"));
            entity.setVmPoolId(Guid.createGuidFromString(rs
                    .getString("vm_pool_id")));
            entity.setName(rs.getString("vm_pool_name"));
            entity.setVmPoolType(VmPoolType.forValue(rs
                    .getInt("vm_pool_type")));
            entity.setParameters(rs.getString("parameters"));
            entity.setPrestartedVms(rs.getInt("prestarted_vms"));
            entity.setVdsGroupId(Guid.createGuidFromString(rs
                    .getString("vds_group_id")));
            entity.setVdsGroupName(rs.getString("vds_group_name"));
            return entity;
        }
    }

    @Override
    public VM getVmDataFromPoolByPoolGuid(Guid vmPoolId, Guid userID, boolean isFiltered) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource()
                .addValue("pool_id", vmPoolId).addValue("user_id", userID).addValue("is_filtered", isFiltered);
        return getCallsHandler().executeRead("GetVmDataFromPoolByPoolId", VMRowMapper.instance, parameterSource);
    }
}
