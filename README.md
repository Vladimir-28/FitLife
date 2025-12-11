## FitLife

Aplicación móvil para el registro y seguimiento de actividad física diaria.

---

## Equipo de desarrollo

- **Bernardo Jese Gonzáles Morales**
- **Katya Hernández Delgado**
- **Vladimir Julio Ramírez**

---

## Descripción del proyecto

**FitLife** es una aplicación Android desarrollada con **Kotlin** y **Jetpack Compose** que permite a las personas llevar un control sencillo y visual de su actividad física diaria.

- **¿Qué hace la app?**
  - Registra actividades diarias con:
    - Día de la semana
    - Número de pasos
    - Distancia recorrida (km)
    - Tiempo activo
  - Muestra estadísticas totales y promedio por día (pasos, distancia, calorías aproximadas).
  - Permite registrar usuarios, iniciar sesión y mantener una sesión activa.
  - Ofrece un flujo completo de **recuperación de contraseña** (solicitar código, verificar y cambiar contraseña).

- **¿Qué problema resuelve?**
  - Muchas personas realizan actividad física pero no llevan un registro consistente.
  - FitLife centraliza la información de pasos/distancia/tiempo para:
    - Visualizar el progreso de forma clara.
    - Motivar hábitos saludables.
    - Tener un historial de actividades sin depender únicamente de apps preinstaladas.

- **¿Qué sensor utiliza?**
  - La app está pensada para utilizar el **acelerómetro / contador de pasos del dispositivo** (sensor de tipo “step counter” o similar) como fuente de datos de pasos.
  - Actualmente, los pasos se envían y almacenan mediante la API propia (servidor Flask + SQLite), lo que permite:
    - Sincronizar actividades con un backend.
    - Consultarlas desde cualquier dispositivo autorizado.

---

## Tecnologías principales

- **Backend**: Python, Flask, Flask‑SQLAlchemy, SQLite.
- **Frontend móvil**: Kotlin, Jetpack Compose, ViewModel + StateFlow.
- **Comunicación**: Retrofit + JSON.


