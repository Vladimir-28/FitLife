# Instrucciones para iniciar el servidor Flask

## Requisitos previos

1. Python 3 instalado
2. Instalar dependencias:
```bash
pip install flask flask-cors flask-sqlalchemy werkzeug
```

## Iniciar el servidor

1. Navegar a la carpeta del servidor:
```bash
cd app/src/main/java/mx/edu/utez/fitlife/data/Api
```

2. Ejecutar el servidor:
```bash
python app.py
```

3. El servidor estará disponible en: `http://0.0.0.0:5000`

## Configurar la IP en la app Android

1. Obtener tu IP local:
   - **Windows**: Ejecutar `ipconfig` y buscar "IPv4 Address"
   - **Mac/Linux**: Ejecutar `ifconfig` o `ip addr`

2. Actualizar `ApiService.kt`:
   - Abrir: `app/src/main/java/mx/edu/utez/fitlife/data/Api/ApiService.kt`
   - Cambiar `BASE_URL` con tu IP: `"http://TU_IP:5000/"`

3. **Nota importante**:
   - Si usas **emulador Android**: usar `"http://10.0.2.2:5000/"`
   - Si usas **dispositivo físico**: usar la IP de tu red local

## Probar la API

Abrir en el navegador: `http://localhost:5000/activities`

Deberías ver un JSON con las actividades de ejemplo.

## Endpoints disponibles

### Actividades
- `GET /activities` - Obtener todas las actividades
- `POST /activities` - Crear nueva actividad
- `PUT /activities/<id>` - Actualizar actividad
- `DELETE /activities/<id>` - Eliminar actividad

### Autenticación
- `POST /auth/register` - Registrar nuevo usuario
  - Body: `{ "name": "Nombre", "email": "email@ejemplo.com", "password": "contraseña" }`
- `POST /auth/login` - Iniciar sesión
  - Body: `{ "email": "email@ejemplo.com", "password": "contraseña" }`

### Usuario por defecto
- Email: `admin@fitlife.com`
- Contraseña: `123456`

