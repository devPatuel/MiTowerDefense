# MiTowerDefense

Tower defense navideño en **Java** (Java 2D / AWT), desarrollado como proyecto de clase en el ciclo de **DAM**. Defiende tu base colocando y mejorando torres para frenar oleadas de Grinches y Santas, con guardado de partida en **MongoDB**.

![Mapa del juego](src/main/resources/img/background2.png)

## Mecánicas

- **7 oleadas** de enemigos con dificultad creciente (número de enemigos, tipo y ritmo de aparición configurados por oleada).
- **Dos tipos de enemigos** que recorren el mapa por waypoints: Grinch (recompensa 10 $) y Santa (más resistente, recompensa 25 $).
- **Economía**: dinero inicial, coste de construcción y de mejora; los enemigos dan monedas al morir.
- **Dos torres construibles** en slots fijos del mapa, cada una mejorable hasta **nivel 3** (más daño, más rango y cambio de sprite):
  - Torre básica — 50 $ (mejora 40 $)
  - Torre de nieve — 120 $ (mejora 80 $)
- **Torre final (base)**: tiene las vidas del jugador y también dispara para defenderse.
- **Disparo predictivo**: las torres calculan el punto de intercepción del proyectil resolviendo la ecuación cuadrática de tiempo de impacto según la velocidad y dirección del enemigo.
- **Fases de juego**: menú inicial (nueva partida / cargar), fase de preparación (construir, mejorar, guardar) y fase de oleada.
- **Guardar y cargar partida** en MongoDB (dinero, vidas, oleada y torres colocadas con su nivel), con un slot de guardado mediante *upsert*.

## Controles

| Acción | Control |
|---|---|
| Seleccionar / colocar torre, mejorar, botones de UI | Clic izquierdo |
| Cancelar selección de torre | Clic derecho |
| Empezar la siguiente oleada | `Enter` |
| Pausa | `P` |
| Reiniciar | `R` |
| Mostrar/ocultar rejilla, debug, hitboxes | `G`, `F3`, `F4` |

## Qué demuestra técnicamente

- **POO aplicada**: jerarquía de entidades (`Entity` → `DynamicEntity` → `AIDynamicEntity` / `WaypointEntity`), herencia y polimorfismo en torres y enemigos.
- **Bucle de juego clásico** con `Canvas` + `BufferStrategy` (doble búfer), hilo de juego propio y desacople entre actualizaciones de lógica (UPS) y renderizado (FPS).
- **Gráficos con Java 2D (AWT)** sin motores externos: sprites, HUD (dinero, vidas, oleada), barras de vida y paneles de compra dibujados a mano.
- **Patrones de diseño**: Singleton (`GameState`), Factory (`EntityFactory`), Repository (`GameRepository`) y escenas intercambiables (`GameScene`).
- **Sistema de colisiones por capas** (`CollisionLayer`) con máscaras entre balas, enemigos y jugador.
- **Persistencia en MongoDB** con el driver oficial síncrono (serialización del estado a documentos BSON y reconstrucción del mapa al cargar).
- **Matemáticas de juego**: clase `Vector2` propia y cálculo de intercepción de proyectiles.

## Requisitos

- JDK 17 o superior (el proyecto usa Gradle 9 mediante wrapper).
- **MongoDB** en `localhost:27017` (solo necesario para guardar/cargar partida; la base de datos `towerdefense` se crea sola).

## Cómo ejecutarlo

```bash
git clone https://github.com/devPatuel/MiTowerDefense.git
cd MiTowerDefense
./gradlew build   # en Windows: gradlew.bat build
```

La clase de entrada es `com.germangascon.gametemplate.Main`. La forma más sencilla de ejecutarla es abrir el proyecto en IntelliJ IDEA (o cualquier IDE con soporte Gradle) y lanzar `Main` desde el IDE.

## Estructura del proyecto

```
src/main/java/com/germangascon/gametemplate/
├── Main.java                  # Punto de entrada
├── core/                      # Motor: Engine (game loop), GameScene, InputManager,
│                              #   AssetManager, Timer, Config, CollisionLayer...
├── math/                      # Vector2 (operaciones vectoriales 2D)
├── entities/                  # Entidades base: Entity, DynamicEntity,
│                              #   AIDynamicEntity, WaypointEntity
├── db/                        # MongoConnection + GameRepository (guardar/cargar)
└── game/
    ├── GameState.java         # Estado global: dinero, vidas, oleada (Singleton)
    ├── WaveManager.java       # Definición y gestión de oleadas
    ├── EntityFactory.java     # Creación de entidades y waypoints del camino
    ├── scenes/                # Escena principal (menú + partida + UI)
    └── entities/              # Tower, SnowTower, FinalTower, Grinch, Santa,
                               #   Bullet, Spawner...
src/main/resources/img/        # Sprites y fondos del juego
```

## Créditos

- Motor base (plantilla `GameTemplate`: game loop, escenas, input, assets): Germán Gascón, dominio público.
- Lógica del juego (torres, enemigos, oleadas, economía, persistencia): Jordi Patuel ([@devPatuel](https://github.com/devPatuel)).
