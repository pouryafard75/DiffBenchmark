/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com>
 */

function toggleWordWrap() {
    // Toggle word wrap
    toggleEditor(window.rightEditor);
    toggleEditor(window.leftEditor);
    function toggleEditor(editor) {
        if (editor) {
            var currentWrapMode = editor.getRawOptions().wordWrap;
            var newWrapMode = currentWrapMode === 'on' ? 'off' : 'on';
            editor.updateOptions({wordWrap: newWrapMode});
        }
    }
}

$(function(){
    let popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    let popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
      return new bootstrap.Popover(popoverTriggerEl)
    })
    $("body").keydown(function(event) {
        if (event.altKey)
        {
            switch (event.code){
                case "KeyT":
                    $('html, body').animate({scrollTop: 0}, 100);
                    break;
                case "KeyB":
                    $("html, body").animate({ scrollTop: $(document).height() }, 100);
                    break;
                case "KeyQ":
                    window.location = "/quit";
                    break;
                case "KeyL":
                    window.location = "/list";
                    break;
                case "KeyW":
                    toggleWordWrap();
                    break;
            }

        }
    });
});