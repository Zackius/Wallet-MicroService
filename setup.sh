#!/bin/bash

# Create necessary directories
mkdir -p rabbitmq
mkdir -p init-db

# Create RabbitMQ definitions file for queue setup
cat > rabbitmq/definitions.json << 'EOF'
{
  "rabbit_version": "3.12.0",
  "rabbitmq_version": "3.12.0",
  "users": [
    {
      "name": "guest",
      "password_hash": "QR7nLK5QLMx24gCKlTJM3Ocuk78gQ1NKHNKdqNwYZr8C9J4H",
      "hashing_algorithm": "rabbit_password_hashing_sha256",
      "tags": ["administrator"]
    }
  ],
  "vhosts": [
    {
      "name": "/"
    }
  ],
  "permissions": [
    {
      "user": "guest",
      "vhost": "/",
      "configure": ".*",
      "write": ".*",
      "read": ".*"
    }
  ],
  "queues": [
    {
      "name": "collection.ledger.request.v1",
      "vhost": "/",
      "durable": true,
      "auto_delete": false,
      "arguments": {}
    },
    {
      "name": "spending.ledger.request.v1",
      "vhost": "/",
      "durable": true,
      "auto_delete": false,
      "arguments": {}
    }
  ],
  "exchanges": [
    {
      "name": "",
      "vhost": "/",
      "type": "direct",
      "durable": true,
      "auto_delete": false,
      "internal": false,
      "arguments": {}
    }
  ],
  "bindings": []
}
EOF

echo "Setup complete!"
echo "Created:"
echo "  - rabbitmq/ directory with configuration"
echo "  - init-db/ directory for database initialization"
echo ""
echo "Next steps:"
echo "1. Run: chmod +x setup.sh && ./setup.sh (if you haven't already)"
echo "2. Run: docker-compose up --build"
echo "3. Your app will be available at: http://localhost:8065"
echo "4. RabbitMQ Management UI: http://localhost:15672 (guest/guest)"
echo "5. Swagger UI: http://localhost:8065/swagger-ui.html"