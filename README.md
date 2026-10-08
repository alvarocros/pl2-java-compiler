👤 Autor
Álvaro López Jiménez — alvarocros

Estudiante de Ingeniería Informática en la UNED

# Java Compiler & Intermediate Code Generator (PL2)

Este repositorio contiene la implementación de un compilador en **Java** desarrollado para la asignatura *Procesadores del Lenguaje II* de la UNED. 

El proyecto abarca desde el análisis del lenguaje fuente hasta la generación de código intermedio y final, siguiendo una arquitectura por fases modular y orientada a objetos.

---

## 🏗️ Arquitectura del Compilador

El sistema está estructurado respetando la separación de responsabilidades en la canalización del compilador:

* **Análisis Léxico (`compiler.lexical`)**: Escaneo e identificación de tokens del lenguaje.
* **Análisis Sintáctico (`compiler.syntax`)**: Construcción del árbol sintáctico e integración con producciones no terminales.
* **Análisis Semántico (`compiler.semantic`)**:
  * Gestión del contexto y ámbito mediante `Scope` y `ScopeManager`.
  * Verificación de tipos (`TypeBase`) y tabla de símbolos.
* **Código Intermedio (`compiler.intermediate`)**: Generación de código intermedio basado en cuadruplas/tres direcciones, utilizando `TemporalFactory` y `LabelFactory`.
* **Código Final (`compiler.code`)**: Emisión del código ensamblador final (`FinalCodeFactory`).
* **Gestor de Contexto (`CompilerContext`)**: Registro centralizado que orquesta la interacción entre las distintas fases y gestores.

---

## 🛠️ Tecnologías y Herramientas

* **Lenguaje:** Java (Programación Orientada a Objetos)
* **Herramientas de Lenguaje:** JFlex / Cup (Generación léxica y sintáctica)
* **Entorno de Desarrollo:** Eclipse / Visual Studio Code
* **Control de Versiones:** Git & GitHub

---

## 🚀 Estructura del Repositorio

```text
├── src/                # Código fuente Java del compilador
├── doc/
│   ├── specs/          # Especificaciones léxicas (.flex) y sintácticas (.cup)
│   └── test/           # Casos de prueba de compilación (.hu, .ens)
├── lib/                # Librerías externas necesarias para la ejecución
└── README.md           # Documentación del proyecto
