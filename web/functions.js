var buffer = [];

onStart = function() {
    buffer = [];
    document.onkeypress = function(e) {
        alert(e.keyCode);
    }
}

onEnd = function() {
    document.onkeypress = function(e) {}
    return buffer;
}

function download(filename, text) {
  var element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  element.setAttribute('download', filename);

  element.style.display = 'none';
  document.body.appendChild(element);

  element.click();

  document.body.removeChild(element);
}
