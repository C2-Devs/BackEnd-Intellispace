Phase 1: The Core 3D Workspace.
This document completely abstracts the human element and focuses strictly on the system architecture, mathematical constraints, and data flow required to build this constraint-driven interior design platform from scratch.
Module 1: Infrastructure & Data Architecture
Before rendering the graphical interface, the persistent data layer must be established to handle high-precision spatial mathematics.
1.1 The Relational Spatial Schema (PostgreSQL 16) Standard integer data types cannot represent WebGL coordinates. The database requires DOUBLE PRECISION or DECIMAL types to ensure millimeter-level accuracy.
* workspaces Table: Defines the mathematical bounds of the room (e.g., room_width, room_length, wall_height).
* workspace_items Table: Stores the exact physical footprint of every object.
    * Position Columns: pos_x, pos_y, pos_z.
    * Rotation Columns: To support full multi-axis rotation, the schema must include rot_x, rot_y, and rot_z (storing Euler angles in radians or degrees).
    * Scale Columns: scale_x, scale_y, scale_z.
      1.2 The Modular Monolith (Spring Boot 3) The backend operates as a Modular Monolith utilizing Hexagonal Architecture (Ports and Adapters).
* Domain Isolation: The workspace module is entirely decoupled from external frameworks, communicating via strictly defined interfaces (Ports).
* Virtual Threads (Project Loom): The Spring Boot server utilizes Java 21 Virtual Threads to handle high-throughput, concurrent I/O bounds when multiple users save layout coordinates simultaneously.

Module 2: Client UI & Application State
The browser environment requires strict DOM control to bridge 2D web interfaces with a 3D WebGL context without degrading performance.
2.1 Viewport Lockdown
* The Next.js application shell uses a rigid overflow-hidden configuration (e.g., 100vh and 100vw) to completely disable native web scrolling. This ensures the mouse wheel strictly interacts with the 3D camera's zoom function rather than scrolling the webpage.
  2.2 Transient Global State (Zustand)
* A centralized state manager holds the active sceneObjects array.
* The Data Contract: The JSON state must map 1:1 with the backend DTOs: { id: string, type: string, position: [x,y,z], rotation: [x,y,z], scale: [x,y,z], isColliding: boolean }.
* Transient Updates: Standard React state (useState) triggers a Virtual DOM re-render on every change. To prevent the UI from freezing when an object updates its coordinates 60 times per second during a drag event, the state manager utilizes transient subscription updates, silently modifying the coordinate memory in the background.

Module 3: 3D Physics & Rendering Engine
This is the mathematical core of the client application, built upon React Three Fiber (R3F) and Three.js.
3.1 Canvas Initialization & Camera Limits
* The WebGL <Canvas> acts as the root portal.
* The scene utilizes OrbitControls for navigation. A strict maxPolarAngle of Math.PI / 2 is enforced to lock the camera from clipping beneath the mathematical floor plane.
  3.2 Translation (Drag & Drop Mechanics)
* While rotation operates on all axes, translation (dragging) must be strictly locked to the X and Z axes.
* If drag mechanics allow free Y-axis translation, users will accidentally float furniture in the air. The Y-coordinate (height) only updates dynamically if an object is intentionally stacked on top of another volumetric mesh.
  3.3 Multi-Axis Rotation Mechanics (X, Y, Z)
* The system utilizes TransformControls (or a similar gizmo) to allow users to rotate meshes on the X (pitch), Y (yaw), and Z (roll) axes.
* Gimbal Lock Prevention: Under the hood, the engine calculates these rotations using Quaternions before converting them to Euler angles for JSON state storage, preventing axes from overlapping and breaking the rotation math during complex spatial manipulation.

Module 4: Aesthetic Generation Engine
This module handles the procedural initialization of the 3D space, translating 2D data into a fully populated, physically viable 3D environment.
* Constraint-Aware Population (REQ-2.1): The generation algorithm ingests the 2D floor plan, Room Type, Budget Tier, and Style Tags. It procedurally places furniture items into the 3D canvas, executing boundary checks to ensure items are initialized without physical collision against walls or other objects.
* High-Fidelity Initialization (REQ-2.2): The system automatically establishes the environmental aesthetics based on the Room Type. This includes generating default ambient lighting, calculating ambient occlusion for depth perception, and rendering foundational wall and ceiling textures (including conditional architectural features like POP ceilings if dictated by the Style Tags).

Module 5: Spatial Collision Advisory System
Real-world objects cannot occupy the same physical space. The system must enforce physical constraints mathematically.
4.1 Volumetric Bounding Boxes
* Every rendered mesh is wrapped in a THREE.Box3—an invisible, mathematically perfect bounding volume.
* Continuous Recalculation: Because objects can rotate on the X and Z axes, their vertical bounding footprint changes drastically (e.g., tipping a tall wardrobe onto its side). The engine must invoke box.setFromObject() on every single animation frame during a transformation to capture the exact new volumetric space.
  4.2 Intersection Logic & Visual Advisory
* The engine utilizes boxA.intersectsBox(boxB) inside a high-frequency loop to detect spatial overlaps.
* Absolute User Agency: The system does not physically block the user's cursor when objects collide. Instead, it alters the intersecting mesh's material color to an emissive red, serving as a visual advisory constraint while preserving user control.

Module 6: Advanced Interactivity (Raycasting & Multi-Axis Transformation)
Users require precise, multi-dimensional control over the 3D space, bridging 2D menus with 3D interactions.
6.1 Raycasting & Selection
* Raycasting (calculating mouse intersections with 3D meshes) is implemented to detect specific object selection.
* Event propagation is strictly stopped (e.stopPropagation()) at the first intersection point to prevent clicks from bleeding through objects into the floor.
* A successful intersection triggers the global state manager to update the activeSelectionId.
  6.2 Two-Way Data Binding
* When an object is actively selected, the 2D UI Sidebar dynamically renders input fields for exact XYZ coordinates and XYZ rotations.
* Inputting a new numerical value into the DOM instantly updates the global state, which in turn teleports or re-orients the 3D object inside the canvas, ensuring perfect synchronization between the UI and WebGL space.
  6.3 Full-Axis Rotation & Bounding Box Recalculation
* The system supports full 3-degree-of-freedom rotation (Pitch on the X-axis, Yaw on the Y-axis, Roll on the Z-axis). This can be manipulated via 3D gizmos (TransformControls) or direct UI input.
* Because rotating an object in multiple dimensions fundamentally alters its physical footprint, the system must immediately trigger a bounding box recalculation (box.setFromObject()) upon any rotation event. This ensures the collision geometry accurately matches the new visual orientation.

Module 7: High-Frequency Network Synchronization
The volatile client-side WebGL state must be securely transported to the persistent PostgreSQL database without overwhelming the network infrastructure.
5.1 The Debounce Gatekeeper
* The frontend network layer monitors the transient state array. It utilizes a debounce algorithm (500ms – 1000ms delay).
* Network transmission is halted while the user is actively translating or rotating an object. Once the user releases the interaction for the duration of the debounce timer, the system extracts the final layout coordinates.
  5.2 Security Payload Integration
* All outbound requests pass through an API interceptor, which attaches a Stateless JSON Web Token (JWT) to the Authorization header.
* The Spring Security Filter Chain validates this signature and asserts the user's role and workspace ownership before allowing the request to proceed.
  5.3 Bulk Transactional Syncing
* The payload is transmitted via a PATCH request to the backend REST API (/api/v1/workspaces/{id}/items).
* Instead of processing individual coordinates, the Spring Boot application accepts the entire array of modified objects, updating them within a single PostgreSQL database transaction to ensure Atomicity and optimize database lock times.
  5.4 Optimistic UI & Standardized Rollbacks
* The client assumes the network request will succeed, updating the 3D scene immediately.
* If the database rejects the payload (e.g., invalid data, expired token), the backend responds with a strict IETF RFC 7807 (Problem Details) JSON error.
* The frontend catches this specific error schema and triggers an immediate rollback, snapping the 3D meshes back to their last confirmed coordinates retrieved from the database. 
