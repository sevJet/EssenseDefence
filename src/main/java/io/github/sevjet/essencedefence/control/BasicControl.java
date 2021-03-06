package io.github.sevjet.essencedefence.control;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.io.IOException;

import io.github.sevjet.essencedefence.entity.Entity;

public abstract class BasicControl extends AbstractControl {

    protected Entity entity;

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial == null)
            return;
        Entity entity = spatial.getUserData("entity");
        if (entity == null) {
            throw new IllegalArgumentException("Not an Entity spatial");
        }
        super.setSpatial(spatial);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Nothing needed here
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);

        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(entity, "entity", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);

        InputCapsule capsule = im.getCapsule(this);
        entity = (Entity) capsule.readSavable("entity", null);
    }
}
