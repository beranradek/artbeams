/**
 * Returns viewport (window) size
 * See: https://stackoverflow.com/questions/3437786/get-the-size-of-the-screen-current-web-page-and-browser-window
 */
function getViewPortSize() {
    var docElem = window.document.documentElement,
        body = window.document.getElementsByTagName('body')[0],
        width = window.innerWidth || docElem.clientWidth || body.clientWidth,
        height = window.innerHeight|| docElem.clientHeight|| body.clientHeight;
    return width + 'x' + height;
}
