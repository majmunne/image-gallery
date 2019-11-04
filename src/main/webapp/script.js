var images;
var request = new XMLHttpRequest();
request.open("GET", "http://gallery-img.eu-central-1.elasticbeanstalk.com/rest/image/get");
request.addEventListener('load', function () {
    if (request.status === 200) {
        images = JSON.parse(request.response);
        for (var i = 0; i < images.length; i++) {
            var imageElement = document.createElement('img');
            imageElement.setAttribute('src', images[i].imageUrl);
            document.querySelector('#image-container').appendChild(imageElement);
        }
    }
});
request.send();
