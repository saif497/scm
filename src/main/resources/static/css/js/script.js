console.log("script loaded");

    let currentTheme=getTheme();

    // start page change theme
// initial

    document.addEventListener('DOMContentLoaded', ()=>{
        changeTheme();
})

//TODO
    function changeTheme(){
// set to web page
        changePageTheme(currentTheme,currentTheme);

// set the listner to change theme button
        const changeThemeButton= document.querySelector('#theme_change_button');


        changeThemeButton.addEventListener("click", (event) =>{
            let oldTheme=currentTheme;
            console.log("change theme button clicked");
            if (currentTheme==="dark"){
// light theme
                currentTheme="light";
            }else {
// dark theme
                currentTheme="dark";
            }
            console.log(currentTheme);
            changePageTheme(currentTheme,oldTheme);
        });
    }

// set theme to local storage
    function setTheme(theme){
        localStorage.setItem("theme", theme);
    }

// get theme from local storage
    function getTheme(){
        let theme=localStorage.getItem("theme");
        if (theme)
            return theme
            else return "light";
    }

// change current page theme
    function changePageTheme(theme,oldTheme){
// local storage me update krege
        setTheme(currentTheme);
// remove the currentTheme
        document.querySelector('html').classList.remove(oldTheme);
// set the current theme
        document.querySelector('html').classList.add(theme);
// change the text of the button
        document.querySelector('#theme_change_button').querySelector("span")
        .textContent = theme=="light" ? "dark" : "light";
    }
//end of page change theme