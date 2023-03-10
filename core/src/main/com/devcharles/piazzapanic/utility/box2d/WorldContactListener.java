package main.com.devcharles.piazzapanic.utility.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import main.com.devcharles.piazzapanic.components.PlayerComponent;
import main.com.devcharles.piazzapanic.components.StationComponent;
import main.com.devcharles.piazzapanic.components.Powerups.PowerupComponent;
import main.com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import main.com.devcharles.piazzapanic.componentsystems.PowerupSpawnSystem;
import main.com.devcharles.piazzapanic.utility.Mappers;
import main.com.devcharles.piazzapanic.utility.Pair;

/**
 * Handles collision events, allows interactivity between the player and other
 * objects.
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Pair<StationComponent, Entity> stationCook = stationInteractResolver(contact);
        if (stationCook != null) {
            stationCook.first.interactingCook = stationCook.second;
            return;
        }

        Pair<Entity, Entity> customerCook = customerInteractResolver(contact);
        if (customerCook != null) {
            // Gdx.app.log("Begin contact", "Cook+Customer");
            Mappers.customer.get(customerCook.first).interactingCook = customerCook.second;
        }
        powerupInteractResolver(contact);
    }

    @Override
    public void endContact(Contact contact) {
        Pair<StationComponent, Entity> stationCook = stationInteractResolver(contact);
        if (stationCook != null) {
            stationCook.first.interactingCook = null;
            return;
        }

        Pair<Entity, Entity> customerCook = customerInteractResolver(contact);
        if (customerCook != null) {
            // Gdx.app.log("End contact", "Cook+Customer");
            Mappers.customer.get(customerCook.first).interactingCook = null;
        }

    }

    /**
     * Things to do upon finishing a contact interaction between cook and station.
     */
    private Pair<StationComponent, Entity> stationInteractResolver(Contact contact) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if (objA == null || objB == null) {
            return null;
        }

        boolean objAStation = (StationComponent.class.isAssignableFrom(objA.getClass()));
        boolean objBStation = (StationComponent.class.isAssignableFrom(objB.getClass()));

        if (objAStation || objBStation) {
            Object station = objAStation ? objA : objB;
            Entity cook = station == objA ? ((Entity) objB) : ((Entity) objA);

            PlayerComponent player = Mappers.player.get(cook);

            if (cook != null && player != null) {
                player.putDown = false;
                player.pickUp = false;
                player.compileMeal = false;
                return new Pair<StationComponent, Entity>((StationComponent) station, cook);
            }
        }
        return null;
    }

    /**
     * Things to do upon finishing a contact interaction between cook and customer.
     */
    private Pair<Entity, Entity> customerInteractResolver(Contact contact) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if (objA == null || objB == null) {
            return null;
        }

        boolean objAEntity = (Entity.class.isAssignableFrom(objA.getClass()));
        boolean objBEntity = (Entity.class.isAssignableFrom(objB.getClass()));

        if (!objAEntity || !objBEntity) {
            return null;
        }

        Entity a = (Entity) objA;
        Entity b = (Entity) objB;

        if (Mappers.customer.has(a) || Mappers.customer.has(b)) {
            Entity customer = Mappers.customer.has(a) ? a : b;
            Entity cook = (customer == a) ? b : a;

            PlayerComponent player = Mappers.player.get(cook);

            if (cook != null && player != null) {
                player.putDown = false;
                player.giveToCustomer = false;
                return new Pair<Entity, Entity>(customer, cook);
            }
        }
        return null;
    }

    private void powerupInteractResolver(Contact contact) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if (objA == null || objB == null) {
            return;
        }

        boolean objAEntity = (Entity.class.isAssignableFrom(objA.getClass()));
        boolean objBEntity = (Entity.class.isAssignableFrom(objB.getClass()));

        if (!objAEntity || !objBEntity) {
            return;
        }

        Entity a = (Entity) objA;
        Entity b = (Entity) objB;
        PowerupComponent boosta = a.getComponent(PowerupComponent.class);
        PowerupComponent boostb = b.getComponent(PowerupComponent.class);
        if (boosta != null && Mappers.player.has(b)) {
            boosta.markedForDeletion = true;
            boosta.playerTouched = b;

        }
        if (boostb != null && Mappers.player.has(a)) {
            boostb.markedForDeletion = true;
            boostb.playerTouched = a;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
