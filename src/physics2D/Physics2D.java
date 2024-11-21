/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D;

import components.propertieComponents.Ground;
import gameEngine.GameObject;
import gameEngine.Transform;
import gameEngine.Window;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
import physics2D.components.Rigidbody2D;
import render.DebugDraw;
import util.Settings;

/**
 * Wrapper para poder usar box2D en la aplicacion
 * @author txaber gardeazabal
 */
public class Physics2D {
    
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);
    
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics2D() {
        world.setContactListener(new ContactListener2D());
    }
    
    /**
     * Añade un gameobject (si este tiene componentes fisicos como rigidbody2D) a box2D
     * @param go el gameobject a añadir
     */
    public void add(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null && rb.getRawBody() == null) {
            createBody(rb, go);
        }
    }
    
    /**
     * Elimina un gameobject de la simulacion de box2D
     * @param go el gameobject a eliminar
     */
    public void destroyGameObject(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }
    
    /**
     * Funcion que se ejecuta en cada frame
     * @param dt delta time
     */
    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }
    
    /**
     * Crea una representacion del boxcollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param boxCollider referencia del box2DCollider
     */
    public void addBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        assert body != null : "raw body must not be null";
        
        PolygonShape shape = new PolygonShape();
        
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        //Vector2f origin = new Vector2f(boxCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }
    
    /**
     * Crea una representacion del circlecollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param circleCollider referencia del circle2DCollider
     */
    public void addCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        assert body != null : "raw body must not be null";
        
        CircleShape shape = new CircleShape();
        
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }
    
    /**
     * Crea una representacion del pillboxcollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param pb referencia del pillboxCollider
     */
    public void addPillboxCollider(Rigidbody2D rb, PillboxCollider pb) {
        Body body = rb.getRawBody();
        assert body != null : "raw body must not be null";
        
        addBox2DCollider(rb, pb.getBox());
        addCircleCollider(rb, pb.getTopCircle());
        addCircleCollider(rb, pb.getBottomCircle());
    }
    
    /**
     * Resetea la representacion del boxcollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param boxCollider referencia del box2DCollider
     */
    public void resetBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;
        
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }
        
        addBox2DCollider(rb, boxCollider);
        body.resetMassData();
    }
    
    /**
     * Resetea la representacion del circlecollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param circleCollider referencia del circle2DCollider
     */
    public void resetCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;
        
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }
        
        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }
    
    /**
     * Resetea la representacion del pillboxcollider dentro de la simulacion de box2D
     * @param rb referencia al rigidboby del collider
     * @param pb referencia del pillboxCollider
     */
    public void resetPillboxCollider(Rigidbody2D rb, PillboxCollider pb) {
        Body body = rb.getRawBody();
        if (body == null) return;
        
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }
        
        addPillboxCollider(rb, pb);
        body.resetMassData();
    }
    
    /**
     * Reinicia el cuerpo en simulacion de un objeto especifico, nota: esto detendra todas las fisicas aplicadas al objeto en cuestion naturalmente.
     * @param rb rigidbody2D donde esta asignado el cuerpo a reiniciar
     * @param go objeto al que resetear las fisicas
     */
    public void resetRigidBody(Rigidbody2D rb, GameObject go) {
        if (rb.getRawBody() != null) {
            world.destroyBody(rb.getRawBody());
        }
        
        createBody(rb, go);
    }

    /**
     * Crea un cuerpo fisico en box2D usando los parametros definidos
     * por un rigidBody y colliders, (solo permite tener un collider del mismo tipo)
     * @param rb Componente rigidbody al que asignar los parametros del cuerpo
     * @param go GameObject al que ira asignado el nuevo cuerpo
     */

    public void createBody(Rigidbody2D rb, GameObject go) {
        Transform transform = go.transform;
            
        BodyDef bodyDef = new BodyDef();
        bodyDef.angle = (float)Math.toRadians(transform.rotation);
        bodyDef.position.set(transform.position.x, transform.position.y);
        bodyDef.angularDamping = rb.getAngularDamping();
        bodyDef.linearDamping = rb.getLinearDamping();
        bodyDef.fixedRotation = rb.isFixedRotation();
        bodyDef.bullet = rb.isContinuousCollision();
        bodyDef.gravityScale = rb.getGravityScale();
        bodyDef.angularVelocity = rb.getAngularVelocity();
        bodyDef.userData = rb.gameObject;

        switch(rb.getBodyType()) {
            case Kinematic: bodyDef.type = BodyType.KINEMATIC; break;
            case Static: bodyDef.type = BodyType.STATIC; break;
            case Dynamic: bodyDef.type = BodyType.DYNAMIC; break;
        }

        Body body = this.world.createBody(bodyDef);
        body.m_mass = rb.getMass();
        rb.setRawBody(body);

        CircleCollider circleCollider;
        Box2DCollider boxCollider;
        PillboxCollider pillboxCollider;

        if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
            addCircleCollider(rb, circleCollider);
        } 

        if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
            addBox2DCollider(rb, boxCollider);
        }

        if ((pillboxCollider = go.getComponent(PillboxCollider.class)) != null) {
            addPillboxCollider(rb, pillboxCollider);
        }
    }
    
    /**
     * Hace un raycast desde el punto A al punto B
     * @param requestingObj objeto desde donde se hizo la llamada al raycast
     * @param pointA punto inicial del raycast
     * @param pointB punto final del raycast
     * @return un objeto con la informacion del raycast
     */
    public RaycastInfo raycast(GameObject requestingObj, Vector2f pointA, Vector2f pointB) {
        RaycastInfo callback = new RaycastInfo(requestingObj);
        world.raycast(callback, new Vec2(pointA.x, pointA.y), new Vec2(pointB.x, pointB.y));
        return callback;
    }
    
    /**
     * Busca las fixtures que tiene un cuerpo fisico dentro de box2D
     * @param body el cuerpo donde coger
     * @return la cantidad de fixtures del cuerpo
     */
    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }
    
    /**
     * Convierte/revierte un rigidbody a un sensor, los sensores dejan pasar objetos fisicos a traves de ellos
     * @param rb la referencia al rigidbody
     * @param isSensor true para pasar a sensor, false para lo contrario
     */
    public void setIsSensor(Rigidbody2D rb, boolean isSensor) {
        Body body = rb.getRawBody();
        if (body == null) return;
        
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = isSensor;
            fixture = fixture.m_next;
        }
    }
    
    public boolean isLocked() {
        return world.isLocked();
    }
    
    /**
     * Busca la fuerza gravitatoria del mundo
     * @return el vector de gravedad del mundo
     */
    public Vector2f getGravity() {
        return new Vector2f(this.gravity.x, this.gravity.y);
    }
    
    /**
     * Detecta si un objeto "esta en el suelo".
     * Realiza dos raycast a cada lado del objeto hacia abajo para buscar objetos solidos, pero solo toma como suelo objetos
     * fisicos con el componente ground
     * @param go el objeto para comprobar
     * @param innerPlayerWidht la distancia aproximada del centro al exterior del objeto
     * @param height la altura aproximada de objeto
     * @return true si se encontro al menos una instancia del componente ground debajo del objeto, false de lo contrario
     */
    public static boolean checkOnGround(GameObject go, float innerPlayerWidht, float height) {
        Vector2f raycastBegin = new Vector2f(go.transform.position);
        raycastBegin.sub(innerPlayerWidht / 2.0f, 0.0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, height);
        
        RaycastInfo info = Window.getPhysics().raycast(go, raycastBegin, raycastEnd);
        
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidht, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidht, 0.0f);
        
        RaycastInfo info2 = Window.getPhysics().raycast(go, raycast2Begin, raycast2End);
        
        if (Settings.mGroundRaycast) {
            DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
         DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));
        }

        return (info.hit && info.hitObj != null && info.hitObj.getComponent(Ground.class) != null)
                || (info2.hit && info2.hitObj != null && info2.hitObj.getComponent(Ground.class) != null);
    }
}
