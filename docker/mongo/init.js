db.createCollection('logs')

db.createUser({
    user: 'pits',
    pwd: 'pits',
    roles: [{
        role: 'readWrite',
        db: 'pits'
    }]
})
