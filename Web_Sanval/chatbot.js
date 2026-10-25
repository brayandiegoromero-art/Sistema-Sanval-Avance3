function toggleChat() {
    const chat = document.getElementById('chatbot');
    chat.style.display = (chat.style.display === 'flex') ? 'none' : 'flex';
}

function handleKeyPress(event) {
    if (event.key === 'Enter') procesarMensaje();
}

function procesarMensaje() {
    const inputField = document.getElementById('userInput');
    const userText = inputField.value.trim().toLowerCase();
    if (userText === '') return;

    agregarMensaje(inputField.value, 'user');
    inputField.value = '';

    setTimeout(() => generarRespuestaIA(userText), 600);
}

function agregarMensaje(texto, emisor) {
    const chatBody = document.getElementById('chat-body');
    const div = document.createElement('div');
    div.className = `mensaje ${emisor}`;
    div.textContent = texto;
    chatBody.appendChild(div);
    chatBody.scrollTop = chatBody.scrollHeight;
}

function generarRespuestaIA(texto) {
    let respuesta = "No encontré ese dato. Intenta preguntar por un SKU (ej. GOR-01) o sobre proveedores y stock.";

    if (texto.includes('gor-01') || texto.includes('clásico')) {
        respuesta = "📊 GOR-01: Tenemos 50 unidades en stock ubicadas en el Estante A1. Precio: S/ 45.00.";
    } else if (texto.includes('som-02') || texto.includes('texano')) {
        respuesta = "⚠️ SOM-02: ¡Stock crítico! Solo quedan 5 unidades en Vitrina 1. Sugiero solicitar reabastecimiento a Curtiembre del Sur S.A.";
    } else if (texto.includes('gor-03') || texto.includes('deportivo')) {
        respuesta = "📊 GOR-03: Contamos con un amplio stock de 120 unidades en el Estante B1. Precio: S/ 35.00.";
    } else if (texto.includes('proveedor') || texto.includes('curtiembre')) {
        respuesta = "🏢 Nuestros proveedores registrados en el sistema son: 'Curtiembre del Sur S.A.' y 'Taller Interno Sanval'.";
    } else if (texto.includes('ubicacion') || texto.includes('estante')) {
        respuesta = "📍 El inventario está distribuido en los Estantes A1, A2, A3, B1, B2, C1, C2 y Vitrina 1 según la categoría del producto.";
    } else if (texto.includes('total') || texto.includes('cuantos productos')) {
        respuesta = "📦 Actualmente la base de datos registra 12 productos activos divididos en 4 categorías principales.";
    }

    agregarMensaje(respuesta, 'bot');
}
document.addEventListener("DOMContentLoaded", () => {
    const reveals = document.querySelectorAll(".reveal");
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add("active");
            }
        });
    }, {
        threshold: 0.15 
    });

    reveals.forEach(reveal => {
        observer.observe(reveal);
    });
});
function procesarCompra(sku) {
    // 1. Mostramos al cliente que se está procesando
    alert("Procesando la compra del artículo: " + sku + "...");

    // 2. Aquí hacemos la petición real a tu servidor Java (Servlet/API)
    // Nota: Esta URL asume que tu servidor Java estará corriendo en el puerto 8080
    fetch('http://localhost:8080/SistemaSanval/Comprar?sku=' + sku, {
        method: 'POST'
    })
    .then(response => {
        if(response.ok) {
            alert("¡Compra exitosa! Se ha descontado 1 unidad del inventario.");
        } else {
            alert("Error: No hay stock suficiente o el servidor está apagado.");
        }
    })
    .catch(error => {
        console.error("Error de conexión con Java:", error);
        alert("Simulación web: Para que el descuento en MySQL ocurra de verdad, tu proyecto en NetBeans debe estar corriendo en un servidor como Apache Tomcat.");
    });
}
function procesarCompra(sku) {
    fetch('http://localhost:8080/Comprar?sku=' + sku, {
        method: 'POST'
    })
    .then(response => {
        if(response.ok) {
            alert("✅ ¡Compra exitosa! Se ha descontado 1 unidad de " + sku + " del inventario real en MySQL.");
        } else {
            alert("❌ Error: No se pudo procesar la compra. Verifica el stock.");
        }
    })
    .catch(error => {
        alert("⚠️ Error de conexión: Asegúrate de que el ApiServidor.java esté ejecutándose en NetBeans.");
    });
}