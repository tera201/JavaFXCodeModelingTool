# JavaFX Code Modeling Tool

JavaFX Code Modeling Tool is a **JavaFX-based library** designed to visualize code structures through **nested circles** in space and city models. This library is used within an **IntelliJ IDEA plugin** to help understand code hierarchies and relationships in an interactive and intuitive way.

## Features
- **Code Visualization**: Represents code structures as nested circular models.
- **Space Models**: Displays code elements in a 3D-like space layout.
- **City Models**: Maps code components to a city-like visualization for better structural understanding.
- **JavaFX-Based**: Utilizes JavaFX for rendering, ensuring smooth and dynamic visualization.


## Usage Example
```java
PackageCircle packageCircle = new PackageCircle("Pack", 1800d, 1673.2421875, 100d);
ClassCircle circle = new ClassCircle("1", 4000, 400, 100);
packageCircle.addAllObjects(circle);
FXSpace<HollowCylinder> fxSpace = new FXSpace<>(packageCircle);
```

## License
This project is licensed under the **Apache 2.0 License**. See the `LICENSE` file for details.

## Credits
Developed by [Roman Naryshkin](https://github.com/tera201).

