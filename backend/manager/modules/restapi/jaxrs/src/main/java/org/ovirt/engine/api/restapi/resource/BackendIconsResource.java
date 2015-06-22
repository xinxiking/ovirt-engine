package org.ovirt.engine.api.restapi.resource;

import org.ovirt.engine.api.model.Icon;
import org.ovirt.engine.api.model.Icons;
import org.ovirt.engine.api.resource.IconResource;
import org.ovirt.engine.api.resource.IconsResource;
import org.ovirt.engine.core.common.businessentities.VmIcon;
import org.ovirt.engine.core.common.queries.VdcQueryParametersBase;
import org.ovirt.engine.core.common.queries.VdcQueryType;

import java.util.List;

public class BackendIconsResource
        extends AbstractBackendCollectionResource<Icon, VmIcon>
        implements IconsResource {

    public BackendIconsResource() {
        super(Icon.class, VmIcon.class);
    }

    @Override
    protected Icon doPopulate(Icon model, VmIcon entity) {
        return model;
    }

    @Override
    public Icons list() {
        return mapCollection(getBackendCollection(VdcQueryType.GetAllVmIcons, new VdcQueryParametersBase()));
    }

    private Icons mapCollection(List<VmIcon> backendEntities) {
        Icons collection = new Icons();
        for (VmIcon icon : backendEntities) {
            collection.getIcons().add(addLinks(map(icon)));
        }
        return collection;
    }

    @Override
    public IconResource getVmIconSubResource(String id) {
        return inject(new BackendIconResource(id));
    }
}