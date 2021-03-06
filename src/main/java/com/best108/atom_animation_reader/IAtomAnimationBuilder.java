package com.best108.atom_animation_reader;

import java.util.Set;

/**
 * The builder for the atom animations.
 *
 * @author thebest108
 */
public interface IAtomAnimationBuilder {

    /**
     * Compiles the information from this builder into an animation. Throws an IllegalStateException
     * if the animation couldn't be built.
     */
    IAtomAnimation build(IModelRenderer modelRenderer);

    Set<String> getModelObjsUsed();
}
