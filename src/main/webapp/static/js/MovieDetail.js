(function ($) {
    function appendPerson(list, name, character, photoUrl, fallbackUrl) {
        var image = $("<img>")
            .addClass("default-img")
            .attr("alt", name)
            .attr("src", photoUrl || fallbackUrl || "../static/images/head.jpg");
        var fallbacks = [];

        if (photoUrl && fallbackUrl && fallbackUrl !== photoUrl) {
            fallbacks.push(fallbackUrl);
        }
        fallbacks.push("../static/images/head.jpg");

        image.on("error", function () {
            var fallback = fallbacks.shift();
            if (fallback) {
                $(this).attr("src", fallback);
            } else {
                $(this).off("error");
            }
        });

        var person = $("<li>").addClass("celebrity");
        var portrait = $("<a>").addClass("portrait").append(image);
        var personName = $("<a>")
            .css("text-decoration", "none")
            .addClass("name")
            .text(character ? name + " 饰 " + character : name);

        person.append(portrait).append($("<div>").append(personName));
        list.append(person);
    }

    window.intitActor = function () {
        var container = $(".celebrity-container").empty();

        $.ajax({
            type: "post",
            url: url + "/movie/findMovieById",
            dataType: "json",
            data: { movie_id: movie_id },
            success: function (response) {
                if (!response || !response.data) {
                    console.error("Movie details were not returned for the cast list.");
                    return;
                }

                var movie = response.data;
                var movieName = movie.movie_cn_name || "";

                if (movie.movie_director) {
                    var directorGroup = $("<div>").addClass("celebrity-group");
                    var directorList = $("<ul>").addClass("celebrity-list clearfix");
                    appendPerson(
                        directorList,
                        movie.movie_director,
                        "",
                        "",
                        "../static/images/actor/" + movieName + "/director/" + movie.movie_director + ".jpg"
                    );
                    directorGroup
                        .append($("<div>").addClass("celebrity-type").text("导演"))
                        .append(directorList);
                    container.append(directorGroup);
                }

                var cast = [];
                if (movie.movie_tmdb_cast) {
                    try {
                        cast = JSON.parse(movie.movie_tmdb_cast);
                    } catch (error) {
                        console.error("Unable to parse the movie cast data.", error);
                    }
                }

                if (Array.isArray(cast) && cast.length > 0) {
                    var castGroup = $("<div>").addClass("celebrity-group");
                    var castList = $("<ul>").addClass("celebrity-list clearfix");
                    cast.forEach(function (person, index) {
                        if (!person || !person.name) {
                            return;
                        }

                        appendPerson(
                            castList,
                            person.name,
                            person.character,
                            person.profile_path,
                            "../static/images/actor/" + movieName + "/actor/" + person.name + ".jpg"
                        );
                    });
                    if (castList.children().length > 0) {
                        castGroup
                            .append($("<div>").addClass("celebrity-type").text("演职人员"))
                            .append(castList);
                        container.append(castGroup);
                    }
                    return;
                }

                var legacyCast = $("<div>").addClass("celebrity-group");
                var legacyCastList = $("<ul>").addClass("celebrity-list clearfix");
                (movie.movie_actor || "")
                    .split(/[,/](?=[^,/:]+:)/)
                    .forEach(function (entry) {
                        if (!entry) {
                            return;
                        }

                        var role = entry.split(":");
                        appendPerson(
                            legacyCastList,
                            role[0],
                            role[1] || "",
                            "",
                            "../static/images/actor/" + movieName + "/actor/" + role[0] + ".jpg"
                        );
                    });
                if (legacyCastList.children().length > 0) {
                    legacyCast
                        .append($("<div>").addClass("celebrity-type").text("演职人员"))
                        .append(legacyCastList);
                    container.append(legacyCast);
                }
            },
            error: function (xhr, status, error) {
                console.error("Unable to load the movie cast.", status, error);
            }
        });
    };
})(jQuery);
