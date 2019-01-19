var buffer = [];

onStart = function() {
    buffer = [];
    document.onkeypress = function(e) {
        buffer.push(e.key);
    }
}

onEnd = function() {
    document.onkeypress = function(e) {}
    return buffer;
}