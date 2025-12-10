from flask import Flask, jsonify, request
from flask_cors import CORS
from models import db, ActivityDay, User
import os
import re

app = Flask(__name__)
CORS(app)  # permite peticiones desde Android
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

# Crear BD al inicio y agregar datos de ejemplo
with app.app_context():
    db.create_all()
    # Crear usuario admin por defecto si no existe (solo para desarrollo)
    if User.query.filter_by(email='admin@fitlife.com').first() is None:
        admin_user = User(name='Administrador', email='admin@fitlife.com')
        admin_user.set_password('123456')
        db.session.add(admin_user)
        db.session.commit()
    # NOTA: Ya no se agregan datos de ejemplo automáticamente
    # Los usuarios deben crear sus propias actividades desde la app

# ----------------------------
# GET: Obtener todas las actividades
# ----------------------------
@app.route('/activities', methods=['GET'])
def get_activities():
    activities = ActivityDay.query.all()
    return jsonify([{
        'id': a.id,
        'day': a.day,
        'steps': a.steps,
        'distanceKm': a.distanceKm,
        'activeTime': a.activeTime
    } for a in activities])

# ----------------------------
# POST: Crear nueva actividad
# ----------------------------
@app.route('/activities', methods=['POST'])
def create_activity():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No se recibieron datos'}), 400
        
        # Validar campos requeridos
        if not all(key in data for key in ['day', 'steps', 'distanceKm', 'activeTime']):
            return jsonify({'error': 'Faltan campos requeridos'}), 400
        
        nueva_actividad = ActivityDay(
            day=data.get('day'),
            steps=data.get('steps'),
            distanceKm=data.get('distanceKm'),
            activeTime=data.get('activeTime')
        )
        db.session.add(nueva_actividad)
        db.session.commit()
        
        return jsonify({
            'id': nueva_actividad.id,
            'day': nueva_actividad.day,
            'steps': nueva_actividad.steps,
            'distanceKm': nueva_actividad.distanceKm,
            'activeTime': nueva_actividad.activeTime
        }), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ----------------------------
# PUT: Actualizar actividad existente
# ----------------------------
@app.route('/activities/<int:id>', methods=['PUT'])
def update_activity(id):
    try:
        activity = ActivityDay.query.get(id)
        if not activity:
            return jsonify({'error': 'Actividad no encontrada'}), 404
        
        data = request.get_json()
        if not data:
            return jsonify({'error': 'No se recibieron datos'}), 400
        
        # Actualizar campos si están presentes
        if 'day' in data:
            activity.day = data.get('day')
        if 'steps' in data:
            activity.steps = data.get('steps')
        if 'distanceKm' in data:
            activity.distanceKm = data.get('distanceKm')
        if 'activeTime' in data:
            activity.activeTime = data.get('activeTime')
        
        db.session.commit()
        
        return jsonify({
            'id': activity.id,
            'day': activity.day,
            'steps': activity.steps,
            'distanceKm': activity.distanceKm,
            'activeTime': activity.activeTime
        }), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ----------------------------
# DELETE: Eliminar actividad
# ----------------------------
@app.route('/activities/<int:id>', methods=['DELETE'])
def delete_activity(id):
    try:
        activity = ActivityDay.query.get(id)
        if not activity:
            return jsonify({'error': 'Actividad no encontrada'}), 404
        
        db.session.delete(activity)
        db.session.commit()
        
        return jsonify({'message': 'Actividad eliminada correctamente'}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ----------------------------
# AUTENTICACIÓN
# ----------------------------

# POST: Registrar nuevo usuario
@app.route('/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No se recibieron datos'}), 400
        
        # Validar campos requeridos
        if not all(key in data for key in ['name', 'email', 'password']):
            return jsonify({'error': 'Faltan campos requeridos'}), 400
        
        name = data.get('name', '').strip()
        email = data.get('email', '').strip().lower()
        password = data.get('password', '')
        
        # Validaciones
        if not name or len(name) < 3:
            return jsonify({'error': 'El nombre debe tener al menos 3 caracteres'}), 400
        
        if not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', email):
            return jsonify({'error': 'Email inválido'}), 400
        
        if not password or len(password) < 6:
            return jsonify({'error': 'La contraseña debe tener al menos 6 caracteres'}), 400
        
        # Verificar si el email ya existe
        if User.query.filter_by(email=email).first():
            return jsonify({'error': 'El email ya está registrado'}), 400
        
        # Crear nuevo usuario
        new_user = User(name=name, email=email)
        new_user.set_password(password)
        db.session.add(new_user)
        db.session.commit()
        
        return jsonify({
            'message': 'Usuario registrado correctamente',
            'user': new_user.to_dict(),
            'token': str(new_user.id)  # Token simple (en producción usar JWT)
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# POST: Iniciar sesión
@app.route('/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No se recibieron datos'}), 400
        
        email = data.get('email', '').strip().lower()
        password = data.get('password', '')
        
        if not email or not password:
            return jsonify({'error': 'Email y contraseña son requeridos'}), 400
        
        # Buscar usuario
        user = User.query.filter_by(email=email).first()
        
        if not user or not user.check_password(password):
            return jsonify({'error': 'Credenciales incorrectas'}), 401
        
        return jsonify({
            'message': 'Login exitoso',
            'user': user.to_dict(),
            'token': str(user.id)  # Token simple (en producción usar JWT)
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    # host='0.0.0.0' permite conexiones desde cualquier IP de la red
    # port=5000 especifica el puerto
    # debug=True activa el modo debug
    app.run(host='0.0.0.0', port=5000, debug=True)
