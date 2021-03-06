package io.github.sevjet.essencedefence.gui;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import io.github.sevjet.essencedefence.entity.Entity;
import io.github.sevjet.essencedefence.util.Configuration;

public class MovementOnGuiControl extends AbstractControl {

    public final static float SCALE_FROM_3D_TO_GUI = 1086.396240234375f;
    private Entity entity;

    public MovementOnGuiControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (((ITextual) entity).isEnded())
            return;
        Vector3f vec = Configuration.getCam().getScreenCoordinates(entity.getGeometry().getWorldTranslation());
        if (vec.getZ() > 1) {
            getSpatial().setCullHint(Spatial.CullHint.Always);
        } else {
            getSpatial().setCullHint(Spatial.CullHint.Never);
        }


        getSpatial().setLocalTranslation(
                Configuration.getCam().getScreenCoordinates(entity.getGeometry().getWorldTranslation()));

        getSpatial().setLocalScale(SCALE_FROM_3D_TO_GUI /
                Configuration.getCam().getLocation().distance(entity.getGeometry().getWorldTranslation()));

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
