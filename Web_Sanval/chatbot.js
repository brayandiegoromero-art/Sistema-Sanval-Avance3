/* =========================================================
   ASISTENTE IA SANVAL
   - Entiende saludos, despedidas, agradecimientos, charla casual
   - Tolera faltas de ortografía, tildes y letras repetidas ("holaaa", "cuanto questa")
   - Recuerda el último producto consultado ("¿y cuánto cuesta?", "¿y el texano?")
   - Recuerda el nombre del usuario ("me llamo Ana")
   ========================================================= */

function toggleChat() {
    const chat = document.getElementById('chatbot');
    chat.style.display = (chat.style.display === 'flex') ? 'none' : 'flex';
    if (chat.style.display === 'flex') {
        const input = document.getElementById('userInput');
        if (input) input.focus();
    }
}

function handleKeyPress(event) {
    if (event.key === 'Enter') procesarMensaje();
}

function procesarMensaje() {
    const inputField = document.getElementById('userInput');
    const textoOriginal = inputField.value.trim();
    if (textoOriginal === '') return;

    agregarMensaje(textoOriginal, 'user');
    inputField.value = '';

    // Indicador de "escribiendo..." para que se sienta más natural
    const indicador = agregarMensaje('Escribiendo...', 'bot');
    indicador.style.opacity = '0.6';
    indicador.style.fontStyle = 'italic';

    const respuesta = generarRespuestaIA(textoOriginal);
    const demora = Math.min(1400, 450 + respuesta.length * 6);

    setTimeout(() => {
        indicador.remove();
        agregarMensaje(respuesta, 'bot');
    }, demora);
}

function agregarMensaje(texto, emisor) {
    const chatBody = document.getElementById('chat-body');
    const div = document.createElement('div');
    div.className = `mensaje ${emisor}`;
    div.style.whiteSpace = 'pre-line'; // permite saltos de línea en las respuestas
    div.textContent = texto;
    chatBody.appendChild(div);
    chatBody.scrollTop = chatBody.scrollHeight;
    return div;
}

/* ------------------ DATOS DEL NEGOCIO ------------------ */

const EMPRESA = {
    telefono: '+51 934 613 623',
    whatsapp: 'https://wa.me/51934613623',
    correo: 'ventas@sanval.com',
    direccion: 'Zona Industrial, Los Olivos - Lima, Perú',
    horario: 'Lunes a Viernes: 8:00 AM - 6:00 PM\nSábados: 9:00 AM - 1:00 PM\nDomingos: cerrado'
};

// especificas = palabras que identifican al producto; genericas = suman poco (ej. "gorro")
const PRODUCTOS = [
    { sku: 'GOR-01', nombre: 'Gorro de Cuero Clásico', precio: 45, cat: 'gorros', stock: 50, ubicacion: 'Estante A1', especificas: ['clasico'], genericas: ['gorro'] },
    { sku: 'GOR-02', nombre: 'Gorro Vintage', precio: 50, cat: 'gorros', especificas: ['vintage', 'retro', 'antiguo'], genericas: ['gorro'] },
    { sku: 'GOR-03', nombre: 'Gorro Deportivo Visera', precio: 35, cat: 'gorros', stock: 120, ubicacion: 'Estante B1', especificas: ['deportivo', 'visera', 'gorra', 'sport'], genericas: ['gorro'] },
    { sku: 'GOR-04', nombre: 'Gorro Aviador con Orejeras', precio: 65, cat: 'gorros', especificas: ['aviador', 'orejeras', 'orejera', 'piloto'], genericas: ['gorro'] },
    { sku: 'GOR-05', art: 'la', nombre: 'Boina Francesa de Cuero', precio: 40, cat: 'gorros', especificas: ['boina', 'francesa', 'frances'], genericas: ['gorro'] },
    { sku: 'GOR-06', nombre: 'Gorro Beanie Cuero Suave', precio: 42, cat: 'gorros', especificas: ['beanie', 'suave', 'bini'], genericas: ['gorro'] },
    { sku: 'SOM-01', nombre: 'Sombrero Ala Ancha', precio: 85, cat: 'sombreros', especificas: ['ala ancha', 'ancha', 'ancho'], genericas: ['sombrero'] },
    { sku: 'SOM-02', nombre: 'Sombrero Texano Cuero', precio: 95, cat: 'sombreros', stock: 5, ubicacion: 'Vitrina 1', especificas: ['texano', 'vaquero', 'cowboy', 'tejano'], genericas: ['sombrero'] },
    { sku: 'SOM-03', nombre: 'Sombrero Pescador Cuero', precio: 55, cat: 'sombreros', especificas: ['pescador', 'bucket'], genericas: ['sombrero'] },
    { sku: 'ACC-01', art: 'los', nombre: 'Guantes de Cuero Térmicos', precio: 30, cat: 'accesorios', especificas: ['guantes', 'guante', 'termicos', 'termico'], genericas: ['accesorio'] },
    { sku: 'ACC-02', art: 'la', nombre: 'Billetera Minimalista Cuero', precio: 25, cat: 'accesorios', especificas: ['billetera', 'cartera', 'wallet', 'minimalista'], genericas: ['accesorio'] },
    { sku: 'ACC-03', nombre: 'Cinturón de Cuero Clásico', precio: 35, cat: 'accesorios', especificas: ['cinturon', 'correa', 'clasico'], genericas: ['accesorio'] }
];

const CATEGORIAS = {
    gorros: ['gorro', 'gorros', 'gorras'],
    sombreros: ['sombrero', 'sombreros'],
    accesorios: ['accesorio', 'accesorios', 'complementos']
};

/* ------------------ INTENCIONES ------------------ */

const INTENCIONES = {
    saludo: ['hola', 'holi', 'hello', 'hi', 'hey', 'buenas', 'buenos dias', 'buenas tardes', 'buenas noches', 'buen dia', 'saludos', 'que onda', 'alo', 'ola', 'wenas', 'habla'],
    despedida: ['adios', 'chau', 'chao', 'hasta luego', 'nos vemos', 'bye', 'hasta pronto', 'me voy', 'hasta manana', 'cuidate'],
    gracias: ['gracias', 'grax', 'thanks', 'agradezco', 'muy amable', 'te pasaste', 'mil gracias'],
    como_estas: ['como estas', 'que tal', 'como te va', 'como andas', 'como vas', 'como te encuentras', 'como has estado', 'todo bien'],
    quien_eres: ['quien eres', 'que eres', 'como te llamas', 'tu nombre', 'eres un bot', 'eres humano', 'eres real', 'eres una ia', 'eres robot', 'con quien hablo'],
    ayuda: ['ayuda', 'ayudame', 'que puedes hacer', 'que sabes', 'opciones', 'menu', 'help', 'que haces', 'en que me ayudas', 'como funciona', 'para que sirves'],
    horario: ['horario', 'horarios', 'hora de atencion', 'atienden', 'abren', 'cierran', 'abierto', 'domingo', 'sabado', 'feriado'],
    ubicacion_tienda: ['donde estan', 'direccion', 'donde quedan', 'donde queda', 'ubicados', 'local', 'tienda', 'tienda fisica', 'sede', 'como llego', 'los olivos', 'donde los encuentro'],
    contacto: ['telefono', 'celular', 'whatsapp', 'wasap', 'numero', 'correo', 'email', 'contactar', 'contacto', 'comunicarme', 'llamar', 'asesor', 'hablar con alguien', 'persona real'],
    envios: ['envio', 'envios', 'delivery', 'despacho', 'mandan', 'envian', 'provincia', 'provincias', 'llega', 'demora', 'domicilio', 'shipping', 'reparto'],
    pago: ['pago', 'pagar', 'pagos', 'yape', 'plin', 'tarjeta', 'transferencia', 'efectivo', 'metodos de pago', 'visa', 'deposito'],
    garantia: ['garantia', 'devolucion', 'devolver', 'cambio', 'defecto', 'fallado', 'reclamo', 'roto'],
    mayor: ['por mayor', 'al por mayor', 'mayorista', 'docena', 'docenas', 'corporativo', 'lote', 'pedido grande', 'empresas'],
    comprar: ['comprar', 'compro', 'carrito', 'ordenar', 'adquirir', 'como compro', 'hacer un pedido', 'quiero uno', 'lo quiero', 'me lo llevo'],
    catalogo: ['catalogo', 'productos', 'que venden', 'que tienen', 'articulos', 'lista', 'modelos', 'coleccion', 'que ofrecen', 'que hay', 'muestrame'],
    precio: ['precio', 'precios', 'cuesta', 'cuestan', 'vale', 'valen', 'costo', 'cuanto', 'cuanto sale', 'a como', 'tarifa'],
    barato: ['mas barato', 'mas economico', 'menor precio', 'barato', 'economico', 'baratos', 'mas accesible'],
    caro: ['mas caro', 'mas costoso', 'mayor precio', 'el mejor', 'mas exclusivo', 'mas fino'],
    stock_bajo: ['stock critico', 'stock bajo', 'poco stock', 'agotado', 'agotados', 'reabastecer', 'reponer', 'reabastecimiento'],
    proveedor: ['proveedor', 'proveedores', 'curtiembre', 'taller interno', 'fabricante', 'quien fabrica', 'quien los hace'],
    ubicacion_inv: ['estante', 'estantes', 'almacen', 'vitrina', 'ubicacion', 'donde esta', 'donde se encuentra', 'donde guardan'],
    total: ['total', 'cuantos productos', 'cantidad de productos', 'categorias', 'inventario', 'resumen'],
    empresa: ['sanval', 'empresa', 'nosotros', 'historia', 'mision', 'vision', 'quienes son', 'quienes somos'],
    calidad: ['cuero genuino', 'genuino', 'material', 'calidad', 'original', 'autentico', 'iso', 'resistente', 'impermeable', 'de que esta hecho'],
    cuidado: ['cuidar', 'limpiar', 'mantenimiento', 'lavar', 'cuidado', 'hidratar', 'se moja'],
    tallas: ['talla', 'tallas', 'medida', 'medidas', 'tamano', 'size', 'ajustable'],
    recomendacion: ['recomienda', 'recomiendas', 'recomendacion', 'sugieres', 'sugerencia', 'regalo', 'regalar', 'frio', 'invierno', 'verano', 'sol', 'playa', 'me conviene', 'aconsejas', 'mas vendido', 'popular'],
    descuento: ['descuento', 'descuentos', 'oferta', 'ofertas', 'promocion', 'promo', 'rebaja', 'cupon'],
    chiste: ['chiste', 'cuentame algo', 'divierteme', 'algo gracioso', 'aburrido'],
    hora: ['que hora es', 'hora actual', 'que dia es', 'fecha de hoy', 'que fecha'],
    insulto: ['tonto', 'estupido', 'idiota', 'inutil', 'no sirves', 'basura', 'pesimo', 'burro'],
    risa: ['jaja', 'jajaja', 'jeje', 'jiji', 'xd', 'lol', 'jsjs', 'jajaj'],
    afirmacion: ['ok', 'okay', 'vale', 'listo', 'perfecto', 'entendido', 'de acuerdo', 'bien', 'genial', 'excelente', 'super', 'chevere', 'bacan', 'si', 'claro', 'dale'],
    negacion: ['no', 'nada', 'ninguno', 'no gracias', 'asi esta bien', 'nada mas']
};

// Orden de prioridad cuando el mensaje contiene varias intenciones
const PRIORIDAD = [
    'stock_bajo', 'proveedor', 'total', 'barato', 'caro', 'recomendacion', 'descuento', 'mayor',
    'comprar', 'envios', 'pago', 'garantia', 'tallas', 'cuidado', 'calidad', 'horario',
    'ubicacion_tienda', 'ubicacion_inv', 'contacto', 'catalogo', 'precio', 'empresa', 'hora',
    'chiste', 'quien_eres', 'ayuda', 'como_estas', 'insulto', 'despedida', 'gracias', 'risa',
    'afirmacion', 'negacion', 'saludo'
];

// Atributos que se pueden preguntar sobre un producto concreto
const ATRIBUTOS = {
    info: ['info', 'informacion', 'detalles', 'detalle', 'ficha', 'caracteristicas', 'datos', 'mas sobre', 'cuentame del', 'hablame del'],
    stock: ['stock', 'hay', 'quedan', 'queda', 'disponible', 'disponibles', 'disponibilidad', 'unidades', 'existencias', 'tienen', 'tienes', 'cuantos hay', 'cuantas hay'],
    precio: INTENCIONES.precio,
    ubicacion: ['estante', 'vitrina', 'ubicacion', 'donde esta', 'donde se encuentra', 'almacen'],
    comprar: INTENCIONES.comprar
};

// Palabras que NUNCA se corrigen por similitud (evita confundir "cuando" con "cuanto")
const SIN_CORRECCION = new Set(['cuando', 'donde', 'como', 'pero', 'para', 'tengo', 'tiene', 'quiero', 'puedo', 'sale', 'vela', 'malo', 'mala', 'menos', 'tanto', 'canto', 'santo', 'hola', 'bola', 'ola', 'tienen', 'tiene', 'tienes', 'quedan', 'cuantos', 'cuantas']);

/* ------------------ MEMORIA DE LA CONVERSACIÓN ------------------ */

const memoria = {
    ultimoProducto: null,
    ultimoAtributo: null,
    nombreUsuario: null,
    fallos: 0,
    saludado: false
};

/* ------------------ PROCESAMIENTO DE LENGUAJE ------------------ */

// Abreviaturas típicas de chat
const ABREVIATURAS = {
    q: 'que', k: 'que', ke: 'que', xq: 'porque', pq: 'porque', porq: 'porque', tb: 'tambien', tmb: 'tambien',
    x: 'por', dnd: 'donde', dnde: 'donde', bn: 'bien', grs: 'gracias', grax: 'gracias', thx: 'gracias',
    ps: 'pues', nd: 'nada', tqm: 'gracias', info: 'info', cel: 'celular', wsp: 'whatsapp', wasap: 'whatsapp',
    pls: 'por favor', plis: 'por favor', xfa: 'por favor', xfavor: 'por favor', cuant: 'cuanto', toy: 'estoy'
};

function normalizar(texto) {
    return texto
        .toLowerCase()
        .normalize('NFD').replace(/[\u0300-\u036f]/g, '')   // quita tildes y ñ -> n
        .replace(/[^a-z0-9\s-]/g, ' ')                       // quita signos y emojis
        .replace(/([aeiou])\1+/g, '$1')                      // "holaaa" -> "hola"
        .replace(/([^aeiou\s\d])\1{2,}/g, '$1')              // "grrrr" -> "gr"
        .replace(/\s+/g, ' ')
        .trim()
        .split(' ')
        .map(p => ABREVIATURAS[p] || p)
        .join(' ');
}

function distancia(a, b) {
    const m = a.length, n = b.length;
    const dp = Array.from({ length: m + 1 }, (_, i) => [i]);
    for (let j = 1; j <= n; j++) dp[0][j] = j;
    for (let i = 1; i <= m; i++) {
        for (let j = 1; j <= n; j++) {
            const costo = a[i - 1] === b[j - 1] ? 0 : 1;
            dp[i][j] = Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + costo);
        }
    }
    return dp[m][n];
}

// Compara una palabra del usuario con una palabra clave tolerando errores de tipeo
function palabraParecida(palabra, clave) {
    if (palabra === clave) return true;
    if (palabra === clave + 's' || palabra === clave + 'es') return true;
    if (clave.length < 5 || SIN_CORRECCION.has(palabra)) return false;
    const tolerancia = Math.min(palabra.length, clave.length) >= 8 ? 2 : 1;
    if (Math.abs(palabra.length - clave.length) > tolerancia) return false;
    return distancia(palabra, clave) <= tolerancia;
}

// Busca una clave (de una o varias palabras) dentro de los tokens del usuario
function contieneClave(tokens, clave) {
    const partes = normalizar(clave).split(' ');
    for (let i = 0; i <= tokens.length - partes.length; i++) {
        let ok = true;
        for (let j = 0; j < partes.length; j++) {
            if (!palabraParecida(tokens[i + j], partes[j])) { ok = false; break; }
        }
        if (ok) return true;
    }
    return false;
}

function contieneAlguna(tokens, claves) {
    return claves.some(c => contieneClave(tokens, c));
}

function detectarIntenciones(tokens) {
    const encontradas = new Set();
    for (const [nombre, claves] of Object.entries(INTENCIONES)) {
        // Afirmaciones/negaciones solo cuentan en mensajes cortos ("ok", "no gracias")
        if ((nombre === 'afirmacion' || nombre === 'negacion') && tokens.length > 3) continue;
        if (contieneAlguna(tokens, claves)) encontradas.add(nombre);
    }
    return encontradas;
}

function detectarProducto(texto, tokens) {
    // 1) Por código SKU: "gor-01", "gor 1", "som02"...
    const sku = texto.match(/\b(gor|som|acc)\s*-?\s*0?([1-9])\b/);
    if (sku) {
        const codigo = `${sku[1].toUpperCase()}-0${sku[2]}`;
        const p = PRODUCTOS.find(x => x.sku === codigo);
        if (p) return { producto: p };
    }
    // 2) Por nombre / palabras clave con puntaje
    let mejores = [], mejorPuntaje = 0;
    for (const p of PRODUCTOS) {
        let puntaje = 0;
        p.especificas.forEach(k => { if (contieneClave(tokens, k)) puntaje += 2; });
        p.genericas.forEach(k => { if (contieneClave(tokens, k)) puntaje += 1; });
        if (puntaje > mejorPuntaje) { mejorPuntaje = puntaje; mejores = [p]; }
        else if (puntaje === mejorPuntaje && puntaje > 0) mejores.push(p);
    }
    if (mejorPuntaje < 2) return null;
    if (mejores.length === 1) return { producto: mejores[0] };
    return { ambiguos: mejores };
}

function detectarAtributo(tokens) {
    for (const [nombre, claves] of Object.entries(ATRIBUTOS)) {
        if (contieneAlguna(tokens, claves)) return nombre;
    }
    return null;
}

function detectarCategoria(tokens) {
    for (const [cat, claves] of Object.entries(CATEGORIAS)) {
        if (contieneAlguna(tokens, claves)) return cat;
    }
    return null;
}

/* ------------------ UTILIDADES DE RESPUESTA ------------------ */

function elegir(opciones) {
    return opciones[Math.floor(Math.random() * opciones.length)];
}

function soles(n) {
    return `S/ ${n.toFixed(2)}`;
}

let tokensActuales = [];

function saludoSegunHora() {
    // Si el usuario usó un saludo concreto, respondemos con el mismo
    if (contieneClave(tokensActuales, 'buenos dias') || contieneClave(tokensActuales, 'buen dia')) return 'Buenos días';
    if (contieneClave(tokensActuales, 'buenas tardes')) return 'Buenas tardes';
    if (contieneClave(tokensActuales, 'buenas noches')) return 'Buenas noches';
    const h = new Date().getHours();
    if (h >= 5 && h < 12) return 'Buenos días';
    if (h >= 12 && h < 19) return 'Buenas tardes';
    return 'Buenas noches';
}

function nombreUsuario() {
    return memoria.nombreUsuario ? `, ${memoria.nombreUsuario}` : '';
}

// "del Gorro...", "de la Billetera..."
function deArticulo(p) {
    return (p.art && p.art !== 'el') ? `de ${p.art} ${p.nombre}` : `del ${p.nombre}`;
}

// "el Gorro...", "la Billetera...", "los Guantes..."
function conArticulo(p) {
    return `${p.art || 'el'} ${p.nombre}`;
}

function capitalizar(t) {
    return t.charAt(0).toUpperCase() + t.slice(1);
}

function listaProductos(lista) {
    return lista.map(p => `• ${p.nombre} (${p.sku}) - ${soles(p.precio)}`).join('\n');
}

function respuestaProducto(p, atributo) {
    switch (atributo) {
        case 'precio':
            return `💰 ${capitalizar(conArticulo(p))} (${p.sku}) cuesta ${soles(p.precio)}.\n¿Quieres saber su disponibilidad o cómo comprarlo?`;
        case 'stock':
            if (p.stock === undefined) {
                return `✅ ${capitalizar(conArticulo(p))} (${p.sku}) ${p.art === 'los' ? 'están disponibles' : 'está disponible'} en nuestro catálogo. Para confirmar las unidades exactas escríbenos por WhatsApp al ${EMPRESA.telefono}.`;
            }
            if (p.stock <= 10) {
                return `⚠️ ${p.sku}: ¡Stock crítico! Solo quedan ${p.stock} unidades ${deArticulo(p)} en ${p.ubicacion}. Sugiero solicitar reabastecimiento a Curtiembre del Sur S.A.`;
            }
            return `📊 ${p.sku}: Tenemos ${p.stock} unidades ${deArticulo(p)} en ${p.ubicacion}. ¡Hay de sobra!`;
        case 'ubicacion':
            if (p.ubicacion) return `📍 ${capitalizar(conArticulo(p))} (${p.sku}) se encuentra en ${p.ubicacion}.`;
            return `📍 ${capitalizar(conArticulo(p))} está en la zona de ${p.cat} del almacén (Estantes A1-C2 y Vitrina 1). Para la ubicación exacta consulta con almacén.`;
        case 'comprar':
            return `🛒 ¡Excelente elección! Para comprar ${conArticulo(p)} (${soles(p.precio)}) ve a la sección Catálogo y presiona "Añadir al Carrito", o pídelo directo por WhatsApp al ${EMPRESA.telefono}.`;
        default: {
            let ficha = `🧢 ${p.nombre}\n• Código: ${p.sku}\n• Precio: ${soles(p.precio)}\n• Categoría: ${capitalizar(p.cat)}`;
            if (p.stock !== undefined) ficha += `\n• Stock: ${p.stock} unidades${p.stock <= 10 ? ' ⚠️ (crítico)' : ''}`;
            if (p.ubicacion) ficha += `\n• Ubicación: ${p.ubicacion}`;
            return ficha + '\n\n¿Te gustaría comprarlo o ver otro producto?';
        }
    }
}

/* ------------------ CEREBRO DEL ASISTENTE ------------------ */

function generarRespuestaIA(textoOriginal) {
    const texto = normalizar(textoOriginal);
    const tokens = texto.split(' ').filter(Boolean);
    tokensActuales = tokens;

    if (tokens.length === 0) {
        return elegir(['😊', '¡Me encantan los emojis! 😄 ¿En qué te puedo ayudar?']);
    }

    // Recordar el nombre del usuario
    const nombre = texto.match(/(?:me llamo|mi nombre es)\s+([a-z]+)/);
    if (nombre) {
        memoria.nombreUsuario = capitalizar(nombre[1]);
        memoria.fallos = 0;
        return `¡Mucho gusto, ${memoria.nombreUsuario}! 😊 Soy el asistente de Sanval S.A.C. ¿Buscas algún gorro, sombrero o accesorio de cuero?`;
    }

    const intenciones = detectarIntenciones(tokens);
    const deteccion = detectarProducto(texto, tokens);
    const atributo = detectarAtributo(tokens);

    // Prefijo amable si el usuario saluda y además pregunta algo
    let prefijo = '';
    if (intenciones.has('saludo')) {
        prefijo = `¡${saludoSegunHora()}${nombreUsuario()}! 👋 `;
        memoria.saludado = true;
    }

    // --- Producto ambiguo ---
    if (deteccion && deteccion.ambiguos) {
        memoria.fallos = 0;
        return prefijo + `Tengo varios productos que coinciden 🤔:\n${listaProductos(deteccion.ambiguos)}\n¿A cuál te refieres?`;
    }

    // --- Producto concreto ---
    if (deteccion && deteccion.producto) {
        const p = deteccion.producto;
        // Si no pregunta algo específico pero es una continuación ("¿y el texano?"), reutiliza lo último preguntado
        const attr = atributo || (tokens.length <= 4 && memoria.ultimoAtributo ? memoria.ultimoAtributo : null);
        memoria.ultimoProducto = p;
        memoria.ultimoAtributo = attr;
        memoria.fallos = 0;
        return prefijo + respuestaProducto(p, attr);
    }

    // --- Intención principal según prioridad ---
    const principal = PRIORIDAD.find(i => intenciones.has(i) && i !== 'saludo');
    const categoria = detectarCategoria(tokens);

    // --- Pregunta de seguimiento sobre el último producto ("¿cuánto cuesta?", "¿y cuántos quedan?") ---
    const seguimientoValido = !principal || ['precio', 'comprar', 'ubicacion_inv', 'afirmacion', 'negacion'].includes(principal);
    if (atributo && memoria.ultimoProducto && tokens.length <= 6 && seguimientoValido && !categoria) {
        memoria.ultimoAtributo = atributo;
        memoria.fallos = 0;
        return prefijo + respuestaProducto(memoria.ultimoProducto, atributo);
    }

    if (principal || categoria) memoria.fallos = 0;

    // Consultas por categoría ("¿qué gorros tienen?", "precios de sombreros")
    if (categoria && (!principal || ['catalogo', 'precio', 'afirmacion', 'comprar'].includes(principal))) {
        const lista = PRODUCTOS.filter(p => p.cat === categoria);
        return prefijo + `Estos son nuestros ${categoria} de cuero 🔥:\n${listaProductos(lista)}\n\n¿Quieres más detalles de alguno?`;
    }

    if (principal) {
        return prefijo + responderIntencion(principal, tokens, categoria);
    }

    // --- Solo saludo ---
    if (intenciones.has('saludo')) {
        const especifico = ['buenos dias', 'buen dia', 'buenas tardes', 'buenas noches'].some(k => contieneClave(tokens, k));
        const hola = especifico ? saludoSegunHora() : 'Hola';
        return elegir([
            `¡${saludoSegunHora()}${nombreUsuario()}! 👋 Bienvenido a Sanval S.A.C. Soy tu asistente virtual. Puedo ayudarte con precios, stock, envíos, horarios y más. ¿Qué estás buscando hoy?`,
            `¡${hola}${nombreUsuario()}! 😊 Qué gusto saludarte. ¿Buscas algún gorro, sombrero o accesorio de cuero? También puedo consultar el inventario por ti.`,
            `¡${hola}${nombreUsuario()}! 🙌 Soy la IA de Sanval. Pregúntame lo que quieras: productos, precios, stock, envíos o cómo comprar.`
        ]);
    }

    // --- No entendió ---
    memoria.fallos++;
    if (memoria.fallos >= 2) {
        memoria.fallos = 0;
        return `Disculpa, todavía no logro entender eso 😅. Puedes preguntarme cosas como:\n• "¿Cuánto cuesta el sombrero texano?"\n• "¿Hay stock del GOR-01?"\n• "¿Hacen envíos a provincia?"\n• "¿Cuál es su horario?"\n\nO si prefieres, habla con un asesor por WhatsApp: ${EMPRESA.telefono}.`;
    }
    return elegir([
        'Mmm, no estoy seguro de haber entendido 🤔. ¿Podrías decirlo de otra forma? Puedo ayudarte con productos, precios, stock, envíos u horarios.',
        'Perdón, no capté bien tu mensaje. ¿Me lo explicas un poco más? Por ejemplo: "precio del gorro aviador" o "¿qué sombreros tienen?".'
    ]);
}

function responderIntencion(intencion, tokens, categoria) {
    const n = nombreUsuario();
    switch (intencion) {
        case 'despedida':
            return elegir([
                `¡Hasta luego${n}! 👋 Gracias por visitar Sanval S.A.C. ¡Vuelve pronto!`,
                `¡Chau${n}! 😊 Fue un gusto ayudarte. Aquí estaré si necesitas algo más.`
            ]);
        case 'gracias':
            return elegir([
                `¡De nada${n}! 😊 ¿Hay algo más en lo que pueda ayudarte?`,
                `¡Con mucho gusto${n}! Para eso estoy 🙌. ¿Necesitas algo más?`,
                `¡Un placer ayudarte${n}! Si tienes otra duda, aquí estoy.`
            ]);
        case 'como_estas':
            return elegir([
                `¡Muy bien, gracias por preguntar${n}! 😄 Listo para ayudarte. ¿Y tú qué tal? ¿Buscas algo en especial?`,
                `¡Excelente! Con el inventario al día y con ganas de ayudarte 💪. ¿En qué te puedo servir?`
            ]);
        case 'quien_eres':
            return '🤖 Soy el asistente virtual con IA de Sanval S.A.C. Estoy conectado al inventario y puedo ayudarte con precios, stock, ubicación de productos, proveedores, envíos, horarios y más. ¡Pregúntame lo que quieras!';
        case 'ayuda':
            return '¡Claro! Esto es lo que puedo hacer por ti 😊:\n• 💰 Precios de cualquier producto\n• 📊 Stock y ubicación en almacén\n• 🧢 Mostrarte gorros, sombreros y accesorios\n• 🎁 Recomendarte productos\n• 🚚 Información de envíos y pagos\n• 🕒 Horarios y dirección\n• 🏢 Proveedores e info de la empresa\n\nSolo escríbeme tu pregunta con tus propias palabras.';
        case 'horario':
            return `🕒 Nuestro horario de atención es:\n${EMPRESA.horario}`;
        case 'ubicacion_tienda':
            return `📍 Nuestra sede principal está en: ${EMPRESA.direccion}.\n🕒 ${EMPRESA.horario.split('\n')[0]}`;
        case 'contacto':
            return `📞 Puedes contactarnos por:\n• WhatsApp / Teléfono: ${EMPRESA.telefono}\n• Correo: ${EMPRESA.correo}\n• Dirección: ${EMPRESA.direccion}\n\nTambién puedes usar el botón verde de WhatsApp en la esquina 😉`;
        case 'envios':
            return '🚚 ¡Sí! Hacemos envíos a todo el Perú mediante nuestra red logística. Usamos un embalaje termorregulado que protege el cuero de la humedad y los cambios de temperatura. Para costos y tiempos según tu ciudad, escríbenos por WhatsApp al ' + EMPRESA.telefono + '.';
        case 'pago':
            return `💳 Para coordinar el método de pago de tu pedido, comunícate con un asesor por WhatsApp al ${EMPRESA.telefono} o al correo ${EMPRESA.correo}. ¡Te atenderán al instante!`;
        case 'garantia':
            return '🛡️ Todos nuestros gorros, sombreros y billeteras tienen 12 meses de garantía por defectos de fábrica. Además contamos con certificaciones ISO en gestión de calidad. Si tienes algún problema, escríbenos a ' + EMPRESA.correo + '.';
        case 'mayor':
            return `📦 ¡Sí atendemos pedidos al por mayor y corporativos! Escríbenos con la cantidad y modelos que necesitas a ${EMPRESA.correo} o por WhatsApp al ${EMPRESA.telefono} y te enviaremos una cotización.`;
        case 'comprar':
            return `🛒 Comprar es muy fácil:\n1. Entra a la sección Catálogo.\n2. Elige tu producto y presiona "Añadir al Carrito".\n\nO si prefieres, pide directamente por WhatsApp al ${EMPRESA.telefono}. ¿Te ayudo a elegir algo?`;
        case 'catalogo':
            return `Tenemos 12 productos de cuero 100% genuino 🧢:\n${listaProductos(PRODUCTOS)}\n\n¿Te interesa alguno en particular?`;
        case 'precio':
            return `💰 Nuestros precios van desde ${soles(25)} hasta ${soles(95)}:\n${listaProductos(PRODUCTOS)}\n\n¿Sobre cuál quieres más información?`;
        case 'barato':
        case 'caro': {
            const lista = categoria ? PRODUCTOS.filter(p => p.cat === categoria) : PRODUCTOS;
            const ordenados = [...lista].sort((a, b) => a.precio - b.precio);
            const p = intencion === 'barato' ? ordenados[0] : ordenados[ordenados.length - 1];
            memoria.ultimoProducto = p;
            const texto = intencion === 'barato' ? 'más económico' : 'más exclusivo';
            return `${intencion === 'barato' ? '💸' : '👑'} El producto ${texto}${categoria ? ' en ' + categoria : ''} es ${conArticulo(p)} (${p.sku}) a ${soles(p.precio)}.`;
        }
        case 'stock_bajo':
            return '⚠️ Alerta de inventario: el Sombrero Texano Cuero (SOM-02) tiene stock crítico con solo 5 unidades en Vitrina 1. Sugiero solicitar reabastecimiento a Curtiembre del Sur S.A.';
        case 'proveedor':
            return "🏢 Nuestros proveedores registrados en el sistema son: 'Curtiembre del Sur S.A.' (cuero de alta calidad) y 'Taller Interno Sanval'.";
        case 'ubicacion_inv':
            return '📍 El inventario está distribuido en los Estantes A1, A2, A3, B1, B2, C1, C2 y Vitrina 1 según la categoría del producto. Dime qué producto buscas y te digo dónde está.';
        case 'total':
            return '📦 Actualmente la base de datos registra 12 productos activos divididos en las categorías: Gorros (6), Sombreros (3) y Accesorios (3).';
        case 'empresa':
            return '🏢 Sanval S.A.C. nació hace más de una década como un pequeño taller de gorros de invierno y hoy es una empresa líder nacional en artículos de cuero. Nuestra misión es crear productos de cuero excepcionales con estilo atemporal, y nuestra visión es ser la marca referente del cuero en el Perú para el 2030.';
        case 'calidad':
            return '✨ Trabajamos con cuero 100% genuino de Curtiembre del Sur S.A. Es flexible, resistente al agua y con acabado premium. Cada pieza pasa pruebas de tensión y resistencia, y contamos con certificación ISO y 12 meses de garantía.';
        case 'cuidado':
            return '🧴 Consejos para cuidar tu artículo de cuero:\n• Límpialo con un paño suave y seco.\n• Si se moja, déjalo secar a la sombra (nunca al sol directo ni con secadora).\n• Hidrátalo con crema para cuero cada 2-3 meses.\n• Guárdalo en un lugar fresco y ventilado.';
        case 'tallas':
            return `📏 Para elegir la talla correcta te recomendamos medir el contorno de tu cabeza (o cintura, en caso del cinturón). Un asesor te ayudará con la medida exacta por WhatsApp al ${EMPRESA.telefono}.`;
        case 'recomendacion': {
            if (contieneAlguna(tokens, ['frio', 'invierno', 'abrigo'])) {
                return '❄️ Para el frío te recomiendo:\n• Gorro Aviador con Orejeras (GOR-04) - S/ 65.00\n• Gorro Beanie Cuero Suave (GOR-06) - S/ 42.00\n• Guantes de Cuero Térmicos (ACC-01) - S/ 30.00';
            }
            if (contieneAlguna(tokens, ['sol', 'verano', 'playa', 'calor'])) {
                return '☀️ Para el sol te recomiendo:\n• Sombrero Ala Ancha (SOM-01) - S/ 85.00\n• Sombrero Pescador Cuero (SOM-03) - S/ 55.00\n• Gorro Deportivo Visera (GOR-03) - S/ 35.00';
            }
            if (contieneAlguna(tokens, ['regalo', 'regalar'])) {
                return '🎁 Para regalar, estos son ideales:\n• Billetera Minimalista Cuero (ACC-02) - S/ 25.00\n• Cinturón de Cuero Clásico (ACC-03) - S/ 35.00\n• Boina Francesa de Cuero (GOR-05) - S/ 40.00\n• Sombrero Texano Cuero (SOM-02) - S/ 95.00 (¡quedan pocos!)';
            }
            memoria.ultimoProducto = PRODUCTOS[0];
            return '⭐ Nuestro producto estrella es el Gorro de Cuero Clásico (GOR-01) a S/ 45.00. También es muy popular el Sombrero Texano (SOM-02). ¿Lo buscas para el frío, para el sol o para regalar?';
        }
        case 'descuento':
            return `🏷️ Para conocer nuestras promociones vigentes y descuentos por volumen, escríbenos por WhatsApp al ${EMPRESA.telefono}. ¡Las compras al por mayor tienen precios especiales!`;
        case 'chiste':
            return elegir([
                '😄 ¿Por qué el sombrero nunca pierde una discusión? ¡Porque siempre está por encima de todo! 🎩',
                '😂 ¿Qué le dijo un gorro a una bufanda? "Tú sigue colgada, que yo me voy a la cabeza." 🧢',
                '🤭 Mi cuero es tan genuino que ni en los chistes miente.'
            ]);
        case 'hora': {
            const ahora = new Date();
            return `🕒 Ahora son las ${ahora.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' })} del ${ahora.toLocaleDateString('es-PE', { weekday: 'long', day: 'numeric', month: 'long' })}.`;
        }
        case 'insulto':
            return 'Lamento no haberte ayudado como esperabas 😔. Sigo aprendiendo. Si quieres, puedes hablar con un asesor humano por WhatsApp al ' + EMPRESA.telefono + '.';
        case 'risa':
            return elegir(['😄 ¡Me alegra que te diviertas! ¿Te ayudo con algo más?', 'Jaja 😂 ¿En qué más te puedo ayudar?']);
        case 'afirmacion':
            return elegir([`¡Perfecto${n}! 👍 ¿Algo más en lo que te pueda ayudar?`, '¡Genial! 😊 Si tienes otra pregunta, aquí estoy.']);
        case 'negacion':
            return elegir([`Está bien${n} 😊. Si luego necesitas algo, aquí estaré.`, 'De acuerdo. ¡Que tengas un excelente día! ✨']);
        default:
            return '¿En qué más te puedo ayudar? 😊';
    }
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