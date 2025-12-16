# Informe del Proyecto:  🪲 BugLog
## Final - Aplicaciones Móviles (ACN4BV)

**Repositorio:** AnthonyNadsat/final-am-acn4bv-galarza-salazar  
**Equipo:** Galarza & Salazar  
**Descripción:** Proyecto Final evolutivo de Aplicaciones Móviles en Escuela Da Vinci

---

## 📋 Descripción

BugLog es una aplicación móvil desarrollada en Android que permite a los usuarios reportar y gestionar bugs encontrados en videojuegos de manera organizada y eficiente.  Cuenta con un sistema de autenticación, roles de usuario (Admin y Tester), formulario interactivo de registro, historial visual con Firebase Realtime Database, funcionalidades de gestión según permisos y sistema de gestión de usuarios. 

---

## ✨ Características

- 🔐 **Sistema de Autenticación:** Login y Logout con Firebase Authentication
- 👥 **Roles de Usuario:** Sistema de permisos diferenciados (Admin y Tester)
- 👤 **Gestión de Usuarios:** Sistema completo para administrar usuarios
- 📝 **Formulario de Reporte:** Interfaz intuitiva para registrar bugs con validación de datos
- 📚 **Historial de Bugs:** Visualización de reportes en formato de tarjetas (cards) con datos en tiempo real
- 🔄 **Navegación Fluida:** Sistema de navegación bidireccional entre vistas
- 🎨 **Diseño Moderno:** Interfaz de usuario completamente rediseñada
- 🔍 **Filtrado:** Botón flotante para filtrar bugs en el historial
- ⬅️ **Navegación Intuitiva:** Botón de retorno para volver a la vista principal
- 🔥 **Integración Firebase:** Base de datos en tiempo real y autenticación de usuarios
- ✏️ **Edición y Eliminación (Admin):** Botones exclusivos para administradores para gestionar bugs

---

## 💿 Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/AnthonyNadsat/final-am-acn4bv-galarza-salazar.git
```

2. **Abrir en Android Studio**
```bash
cd final-am-acn4bv-galarza-salazar
```
Luego abre el proyecto desde Android Studio


3. **Sincronizar Gradle**
   - Android Studio sincronizará automáticamente las dependencias

4. **Ejecutar la aplicación**
   - Conecta un dispositivo Android o inicia un emulador
   - Presiona el botón "Run" en Android Studio


   ```
   final-am-acn4bv-galarza-salazar/
   └── app/
       └── google-services.json
   ```
   - **Importante:** Este archivo contiene configuraciones sensibles y no debe ser compartido públicamente.  No está incluido en el repositorio por seguridad. 


---

## 📱 Capturas de Pantalla

### Pantalla Primaria - Login
El usuario abre la aplicación
Ingresa su nombre de usuario (admin o tester)
Ingresa la contraseña (123456)
Presiona el botón "Ingresar"
Si las credenciales son correctas → Redirección a MainActivity
Si las credenciales son incorrectas → Mensaje de error y permanece en login 

Campo usuario no puede estar vacío
Campo contraseña no puede estar vacío
Muestra error en el campo correspondiente si falta dato


Autenticación: 

Convierte usuario a email:  {usuario}@buglog.com
Llama a FirebaseAuth.signInWithEmailAndPassword()
Maneja respuestas asíncronas con listeners


Usuarios válidos: 

admin@buglog.com - Permisos de administrador (editar/borrar bugs)
tester@buglog. com - Permisos de tester (solo reportar/ver bugs)


Persistencia de sesión:

Si ya existe usuario autenticado (auth. getCurrentUser() != null)
Redirección automática a MainActivity sin mostrar login

![Pantalla1](https://github.com/user-attachments/assets/3855aa7c-df49-4e04-af6c-5cc363caacdd)

![Login buglog actualizado](https://github.com/user-attachments/assets/19d58943-4c47-4029-9538-1e6d6a598aa1)



### Pantalla Secundaria - Formulario de Reporte

El usuario completa el campo "Nombre del juego"
Selecciona la plataforma del menú desplegable
Selecciona el tipo de bug del menú desplegable
Marca la gravedad usando los radio buttons (Baja, Media o Alta)
Escribe la descripción del bug en el área de texto
(Opcional) Pega la URL de una imagen de portada
Presiona el botón "Enviar reporte"
Si todo está completo → Bug guardado en Firebase, mensaje de éxito, formulario limpio
Si falta algún campo → Mensaje de error indicando qué campo falta

<img width="390" height="995" alt="wireframe_form" src="https://github.com/user-attachments/assets/ec487ba9-3c0f-46d0-9b43-a54ab46e6c93" />

![reportebugs](https://github.com/user-attachments/assets/cd858dec-1a63-4b92-87dc-ab1e215d5502)




### Pantalla Terciaria - Historial de Bugs

El usuario accede desde MainActivity presionando "Consultar historial"
El sistema carga y muestra todos los bugs reportados en formato de cards

El usuario puede: 
Scroll para ver todos los bugs
Filtrar presionando el botón flotante y seleccionando una gravedad (Todos/Baja/Media/Alta)
Volver a MainActivity presionando la flecha "←"


Si el usuario es admin, cada card tiene botones "Editar" y "Borrar"
Para editar: Presiona "Editar" → Se abre modal con datos → Modifica campos → Presiona "Guardar" → Bug actualizado
Para borrar:  Presiona "Borrar" → Bug eliminado inmediatamente de Firestore y desaparece de la lista

<img width="390" height="995" alt="wireframe_list" src="https://github.com/user-attachments/assets/3ef0142f-b04c-4e65-879b-96577f7fb6f7" />


![Historial de reportes](https://github.com/user-attachments/assets/55eb0bce-1a03-48d3-81ab-16fa58ed3edc)



### Pantalla Cuarta - Gestión de Usuarios
El usuario administrador accede desde MainActivity presionando "Gestión de Usuarios" El sistema carga y muestra todos los usuarios registrados en formato de cards

Cada card muestra:

Badge de rol (ADMINISTRADOR/TESTER)
Nombre, email y fecha de creación
Botones "Eliminar" y "Editar"
El administrador puede:

Ver todos los usuarios registrados
Crear nuevo usuario presionando "+ Nuevo" → Completa nombre, email, contraseña y rol → Presiona "CREAR"
Editar usuario presionando "Editar" → Modifica campos → Presiona "Guardar"
Eliminar usuario presionando "Eliminar" → Usuario eliminado de Firestore
Volver a MainActivity con la flecha "←"
⚠️ Nota: Pantalla exclusiva para administradores

![Creacion de usario buglog](https://github.com/user-attachments/assets/424f621b-606c-4ecf-a5d1-a003f301e3b4)

![usuario creado ](https://github.com/user-attachments/assets/7b2c0832-8fa7-4880-a003-901553367a0e)


---

## 📊 Resumen

Este proyecto corresponde al desarrollo colaborativo evolutivo de una aplicación Android llamada **BugLog**, diseñada para el reporte y seguimiento de bugs/glitches encontrados en videojuegos.  Incluye autenticación de usuarios, sistema de roles, integración con Firebase, gestión completa de reportes según permisos y sistema de gestión de usuarios. 

---

## 💻 Tecnologías y Estadísticas

### Lenguajes de Programación
- **Java**

### Tecnologías y Servicios
- **Firebase Authentication:** Sistema de login y gestión de usuarios
- **Firebase Firestore:** Almacenamiento y sincronización de datos en tiempo real
- **Glide:** Carga de imágenes desde URLs



### Información del Repositorio
- **Visibilidad:** Público
- **Rama principal:** master
- **Colaboradores:** AnthonyNadsat, Lukarda

---

## 📅 Línea de Tiempo del Proyecto

### **Fase 1: Configuración Inicial (Septiembre 2025)** 

#### Commit 1: Estructura Base
- **Autor:** AnthonyNadsat
- **Hash:** `c4fac99`
- **Fecha:** 29 de septiembre, 2025
- **Mensaje:** `chore(base-setup): Estructura inicial del proyecto Android`
- **Descripción:** Configuración base del proyecto Android con estructura de carpetas y archivos iniciales. 

---

### **Fase 2: Desarrollo del Formulario (Septiembre 2025)**

#### Commit 2: Pantalla de Reporte
- **Autor:** AnthonyNadsat
- **Hash:** `413560b`
- **Fecha:** 29 de septiembre, 2025
- **Mensaje:** `feat(form): pantalla de reporte con formulario`
- **Descripción:** Implementación de la pantalla principal con formulario interactivo para reportar bugs, incluyendo campos de entrada y validaciones.

#### Commit 3: Sistema de Historial
- **Autor:** Lukarda
- **Hash:** `088f367`
- **Fecha:** 29 de septiembre, 2025
- **Mensaje:** `feat(list): lista de bugs con tarjetas agregadas a modo de historial`
- **Descripción:** Creación del sistema de visualización de bugs registrados mediante 'cards' en una segunda pantalla.

---

### **Fase 3: Rediseño de Interfaz (Octubre 2025)**

#### Commit 4: Rediseño Completo
- **Autor:** AnthonyNadsat
- **Hash:** `0b007cc`
- **Fecha:** 1 de octubre, 2025
- **Mensaje:** `feat(ui): rediseño completo de BugLog (formulario e historial)`
- **Descripción:** Renovación de la interfaz de usuario con mejoras visuales tanto del formulario como del historial de bugs. 

---

### **Fase 4: Mejoras de Navegación (Octubre 2025)**

#### Commit 5: Botón de Retorno
- **Autor:** Lukarda
- **Hash:** `ac73b68`
- **Fecha:** 2 de octubre, 2025
- **Mensaje:** `feat(list): boton volver para regresar a la vista principal`
- **Descripción:** Implementación de un botón para retornar desde el historial a la pantalla principal. 

#### Commit 6: Recuperación de Archivos
- **Autor:** Lukarda
- **Hash:** `ee7245b`
- **Fecha:** 2 de octubre, 2025
- **Mensaje:** `fix(list): recupera archivos de pantalla de historial`
- **Descripción:** Corrección y recuperación de archivos relacionados con la pantalla de historial. 

---

### **Fase 5: Refinamiento Final - Parcial 1 (Octubre 2025)**

#### Commit 7: Botón Flotante y Ajustes Finales
- **Autor:** Lukarda y AnthonyNadsat
- **Hash:** `6ff6925`
- **Fecha:** 6 de octubre, 2025
- **Mensaje:** `feat(list-ui): boton flotante de filtro y ajustes visuales en historial de bugs`
- **Descripción:** Implementación de botón flotante para funcionalidad de filtrado y refinamiento en la experiencia de usuario del historial.

#### Commit 8: Informe del Parcial 1
- **Autor:** AnthonyNadsat
- **Hash:** `f30e0c4`
- **Fecha:** 6 de octubre, 2025
- **Mensaje:** `docs(readme): informe del parcial`
- **Descripción:** Creación del informe completo del Parcial 1 con documentación detallada del proyecto.

#### Commit 9: Wireframe Agregado
- **Autor:** AnthonyNadsat
- **Hash:** `4c7c6da`
- **Fecha:** 7 de octubre, 2025
- **Mensaje:** `fix(readme): wireframe faltante agregado`
- **Descripción:** Incorporación de wireframes faltantes en la documentación del README. 

---

### **Fase 6: Integración Firebase - Inicio Parcial 2 (Noviembre 2025)**

#### Commit 10: Configuración Firebase y Firestore
- **Autor:** AnthonyNadsat
- **Hash:** `1db476a`
- **Fecha:** 26 de noviembre, 2025
- **Mensaje:** `feat:  reemplazado almacenamiento local e integrado Firebase Firestore como base de datos`
- **Descripción:** Migración completa del almacenamiento local a Firebase Firestore para persistencia de datos en la nube.

---

### **Fase 7: Sistema de Autenticación y Roles (Noviembre 2025)**

#### Commit 11: Login con Firebase Auth
- **Autor:** Lukarda
- **Hash:** `84720d1`
- **Fecha:** 27 de noviembre, 2025
- **Mensaje:** `feat:  implementado login con Firebase Auth y control de roles`
- **Descripción:** Desarrollo completo del sistema de autenticación con Firebase Auth y diferenciación de roles (Admin/Tester).

---

### **Fase 8: Funciones de Administrador (Noviembre 2025)**

#### Commit 12: Botones de Editar y Borrar Parcial
- **Autor:** Lukarda
- **Hash:** `0a53192`
- **Fecha:** 27 de noviembre, 2025
- **Mensaje:** `feat(admin): implementacion parcial de botones de editar y borrar en lista de bugs`
- **Descripción:** Primera implementación de funcionalidades de administrador para editar y borrar bugs. 

#### Commit 13: Modal de Edición Completo
- **Autor:** AnthonyNadsat
- **Hash:** `594f939`
- **Fecha:** 27 de noviembre, 2025
- **Mensaje:** `feat(admin): implementado boton para editar con modal en lista de bugs`
- **Descripción:** Implementación completa del modal de edición con todos los campos editables para administradores.

---

### **Fase 9: Mejoras Visuales y Funcionalidades Extra (Noviembre 2025)**

#### Commit 14: Logout Funcional
- **Autor:** Lukarda
- **Hash:** `cf687a4`
- **Fecha:** 27 de noviembre, 2025
- **Mensaje:** `feat:  logout funcional integrado`
- **Descripción:** Implementación del botón de logout con cierre de sesión correcto en Firebase Auth.

#### Commit 15: Carga de Imágenes desde URL
- **Autor:** Lukarda
- **Hash:** `65b5bf1`
- **Fecha:** 27 de noviembre, 2025
- **Mensaje:** `feat:  agregado campo de URL y carga de imagenes en reportes`
- **Descripción:** Integración de Glide para cargar imágenes desde URLs en los reportes de bugs.

#### Commit 16: Rediseño Visual Final Parcial 2
- **Autor:** AnthonyNadsat
- **Hash:** `ab6a5cf`
- **Fecha:** 28 de noviembre, 2025
- **Mensaje:** `feat:  rediseño a pantalla de login y mejoras visuales en lista de bugs`
- **Descripción:** Rediseño completo de la interfaz de login con diseño moderno centrado y mejoras visuales en el historial con cards horizontales y formato de póster para imágenes.

#### Commit 17: Informe del Parcial 2
- **Autor:** Lukarda
- **Hash:** `91f75af`
- **Fecha:** 28 de noviembre, 2025
- **Mensaje:** `docs(readme): informe del parcial 2`
- **Descripción:** Informe de parcial evolutivo con documentación actualizada. 

---

### **Fase 10: Final - Nuevas Funcionalidades (Diciembre 2025)**

#### Commit 18: Rediseño Completo Final
- **Autor:** AnthonyNadsat
- **Hash:** `7d3a1dc`
- **Fecha:** 12 de diciembre, 2025
- **Mensaje:** `feat: nuevo rediseño completo de BugLog`
- **Descripción:** Rediseño completo de la aplicación BugLog con mejoras visuales para la entrega final.

#### Commit 19: Sistema de Gestión de Usuarios
- **Autor:** Lukarda
- **Hash:** `8bc5f66`
- **Fecha:** 16 de diciembre, 2025
- **Mensaje:** `feat: implementado sistema de gestion de usuarios`
- **Descripción:** Implementación completa del sistema de gestión de usuarios para administradores. 

---

## 🎯 Funcionalidades Implementadas

### ✅ Módulo de Autenticación
- Sistema de Login con Firebase Authentication
- Registro de nuevos usuarios
- Logout y gestión de sesiones
- Validación de credenciales

### ✅ Módulo de Roles
- Rol **Admin**: Acceso completo con permisos de edición y eliminación
- Rol **Tester**:  Acceso para reportar y visualizar bugs
- Validación de permisos según el rol del usuario

### ✅ Módulo de Gestión de Usuarios
- Sistema completo para administrar usuarios
- Control y visualización de usuarios registrados

### ✅ Módulo de Registro
- Formulario completo de reporte de bugs
- Validación de datos de entrada
- Interfaz de usuario moderna
- Integración con Firebase Database

### ✅ Módulo de Historial
- Visualización de bugs en formato de tarjetas
- Sistema de navegación fluida entre pantallas
- Botón flotante para filtrado
- Actualización en tiempo real con Firebase
- Botones de Editar y Eliminar (solo para Admin)

### ✅ Sistema de Navegación
- Navegación bidireccional entre vistas
- Botón de retorno a vista principal

### ✅ Integración Firebase
- Firebase Authentication para login/logout
- Firebase Firestore para almacenamiento
- Sincronización de datos en tiempo real
- Configuración mediante google-services.json

---

## 🔧 Aspectos Técnicos

### Tecnologías Utilizadas
- **Plataforma:** Android
- **Lenguaje Principal:** Java
- **IDE:** Android Studio 
- **Build System:** Gradle
- **Patrón de diseño:** Activities
- **Backend:** Firebase/Firestore

### Archivos Principales
- `MainActivity.java` - Pantalla de login y autenticación
- `FormularioActivity.java` - Actividad para reportar bugs
- `ListaBugsActivity.java` - Actividad principal para listar y gestionar bugs
- `Bug.java` - Modelo de datos para bugs
- `BugAdapter.java` - Adaptador para RecyclerView
- Archivos de layout XML para formularios, listas y login
- Recursos drawable para elementos visuales
- Configuraciones Gradle para dependencias
- `google-services.json` - Configuración de Firebase (no incluido en el repositorio por seguridad)

---

## 🔄 Flujo de Trabajo

El equipo utilizó un flujo de trabajo colaborativo basado en Git: 

1. **Desarrollo paralelo** - Ambos desarrolladores trabajaron en diferentes módulos simultáneamente
2. **Integración continua** - Merge regular de cambios a la rama master
3. **Conventional Commits** - Uso de prefijos semánticos (feat, fix, chore, refactor)
4. **Desarrollo evolutivo** - Final construido sobre la base de los Parciales 1 y 2

### Patrones de Commit Utilizados
- `feat(módulo):` - Nuevas funcionalidades
- `fix(módulo):` - Corrección de bugs
- `chore(módulo):` - Tareas de mantenimiento
- `docs(módulo):` - Documentación
- `refactor(módulo):` - Refactorización y optimización

---

## 🏆 Logros del Proyecto

### Parcial 1
- ✅ Aplicación funcional y completa
- ✅ Interfaz moderna
- ✅ Navegación intuitiva
- ✅ Colaboración efectiva entre los colaboradores
- ✅ Uso de conventional commits

### Parcial 2 (Evolutivo)
- ✅ Integración exitosa con Firebase/Firestore
- ✅ Sistema de autenticación 
- ✅ Roles de usuario implementados
- ✅ Implementación de URL para Imágenes
- ✅ Permisos diferenciados (Admin/Tester)
- ✅ Sincronización en tiempo real
- ✅ Gestión completa de bugs mediante CRUD
- ✅ Rediseño visual de la interfaz

### Final (Evolutivo)
- ✅ Nuevo rediseño completo de la aplicación
- ✅ Sistema de gestión de usuarios implementado
- ✅ Mejoras visuales finales
- ✅ Aplicación completamente funcional y pulida

---

## 📝 Conclusiones

El proyecto **BugLog** fue desarrollado evolutivamente a lo largo de tres entregas:  Parcial 1, Parcial 2 y Final.  Cada fase construyó sobre la anterior, agregando nuevas funcionalidades y mejoras visuales.  El resultado final es una aplicación Android completa y funcional para la gestión de bugs en videojuegos, con autenticación de usuarios, roles diferenciados, y un sistema de gestión de usuarios para administradores. 
