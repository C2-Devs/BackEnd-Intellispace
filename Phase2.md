
Phase 2: The Architect's "High-Impact" Curated Catalog & Deep Customization.
This document outlines the pure engineering mechanics of the platform. It details how the WebGL engine, relational database, state manager, and procedural generation algorithms interlock to create a highly customizable, constraint-aware, and financially tracked 3D environment.
Module 1: The Cloud Asset Pipeline
To support programmatic manipulation in the browser, all underlying 3D assets must be mathematically normalized before injection into the system.
* Asset Normalization: Every base .glb model (e.g., a blank sofa, a modular cabinet) is exported at a strict 1:1 scale, where 1 WebGL unit equals exactly 1 real-world meter.
* Mesh Segregation: The 3D models are constructed with strict internal hierarchies. Sub-meshes (e.g., Sofa_Cushion, Sofa_Legs) are physically separated and explicitly named within the file so client-side graph traversal algorithms can target them individually.
* Mathematical UV Unwrapping: All models require pristine UV maps. This 2D flattening of the 3D surface dictates precisely how the WebGL engine wraps a 2D texture (like wood grain) around complex geometry without visual distortion.
* Zero-Egress CDN Delivery: The normalized models and high-resolution Physically Based Rendering (PBR) texture maps are stored in object storage and streamed directly to the client via an Edge CDN, ensuring minimal latency and zero bandwidth egress costs.

Module 2: Relational Catalog & Constraint Engine
The backend database evolves from storing basic coordinate states to managing a complex relational matrix of physical constraints, pricing, and architectural styles.
* Catalog Schemas:
    * base_models: Stores intrinsic physical properties (ID, category, CDN URL, default dimensions, and parametric scaling limits).
    * materials: Stores PBR texture sets, mapping a material ID to its specific CDN URLs for Albedo, Normal, and Roughness maps.
    * moodboards: Defines architectural curation tags (e.g., "Minimalist Scandi", "Industrial Work").
* The Compatibility Matrix: A strict relational join table enforcing physical realism. If the client queries a curved modern sofa, this matrix ensures the API only returns structurally compatible fabrics or leathers, actively rejecting rigid materials like wood or metal.
* Semantic Vector Preparation: Vector embeddings are generated for every base model and moodboard style, laying the infrastructural database groundwork for semantic similarity searches.
  Module 3: Dynamic Budget Engine
  This module runs concurrently with the spatial engine, calculating financial impact at 60 FPS without waiting for backend network responses.
* Relational Pricing Matrix: The database stores modular pricing data. base_models hold a base manufacturing price, materials hold a price multiplier (e.g., premium leather = 2.5x), and parametric items hold a unit-scale coefficient (cost per added meter).
* Real-Time Calculation (REQ-4.1): The global state manager utilizes a derived state selector that mathematically aggregates the estimated cost of all items currently in the 3D canvas. This calculation is strictly triggered upon spatial generation, item addition, item deletion, and material swaps.
* Budget Threshold Alerts (REQ-4.2): The UI maintains a continuous financial feedback loop. If the real-time calculated cost exceeds the user's defined maximum budget, the system activates a visual warning indicator on the budget tracker. Enforcing the "Absolute User Agency" philosophy, the system warns the user of the financial overrun but does not block the aesthetic action.
  Module 4: Dynamic Material Swapping & PBR Injection
  This module handles the real-time application of hyper-realistic textures to 3D geometry based on user configuration.
* State-Driven UI Rendering: When an object is selected in the WebGL canvas, the state manager queries the constraint engine. The 2D UI dynamically renders only the material swatches legally permitted for that specific model.
* WebGL Mesh Traversal: Upon loading the base model, the system executes a traversal algorithm across the parsed 3D graph, isolating the specific target nodes required for aesthetic mutation.
* PBR Texture Construction: When a new material is selected, the system asynchronously downloads the associated maps. A new PBR material is dynamically constructed in memory and injected onto the isolated node, altering its physical appearance in real-time without requiring a new 3D model download.
  Module 5: Exact Scaling & Parametric Mathematics
  Interior architecture requires millimeter-perfect constraints. Resizing objects introduces severe mathematical challenges regarding texture integrity and collision geometry.
* Non-Uniform Texture Tiling: Standard 3D scaling mathematically stretches the applied texture map. The system mitigates this by dynamically adjusting the UV repeat properties. If a cabinet's width is scaled by 1.5x, the applied texture is automatically calculated to tile 1.5x across the surface, preserving visual realism.
* Anchor Points & Pivot Constraints: Scaling algorithms are bound to specific pivot points. A modular cabinet designed to snap to a wall scales outward exclusively from its back edge, preventing it from clipping through the virtual boundary.
* Real-Time Bounding Box Recalculation: Any parametric dimension change fundamentally alters the object's physical footprint. The system ensures that executing a scale adjustment instantly triggers a mathematical bounding box recalculation. This feeds the exact new dimensions directly back into the Collision Advisory engine to maintain accurate overlap detection.
  Module 6: Network Synchronization & State Expansion
  The network payload and throttling mechanisms are expanded to handle continuous parametric and financial adjustments.
* Expanded Data Payload: The HTTP client and backend API contracts are extended. The debounced synchronization payload now includes materialId, parametric scale factors (scaleX, scaleY, scaleZ), and the aggregated current_cost for the entire workspace.
* High-Frequency Slider Debouncing: Parametric scaling via UI sliders generates hundreds of state mutations per second. While the WebGL and Budget engines calculate the math instantly on the client side, the network layer enforces a strict debounce threshold. The system holds the synchronization request until the slider input ceases for a defined interval, ensuring the backend is only hit with the final, resolved state.
