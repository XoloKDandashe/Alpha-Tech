firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.

    document.getElementById("user_div").style.display = "block";
   document.getElementById("login_div").style.display = "none";
  //  window.location='home.html';
    var user = firebase.auth().currentUser;
	
    if(user != null){

      var email_id = user.email;
		var photoUrl =document.getElementById("para");
		var firebaseref = firebase.database().ref('/users/'+user.uid).child("imageUrl");
		firebaseref.on('value',function(snapshot){
  //		photoUrl.innerText="hi"+snapshot.val();
      if(snapshot.val()!= null){
        var image= document.getElementById("photo");
        image.src=snapshot.val();
      }
		
    });

    return firebase.database().ref('/users/'+user.uid).once('value').then(function(snapshot){
      document.getElementById("name").innerHTML=(snapshot.val() && snapshot.val().fullname)||'Anonymous';
      document.getElementById("company").innerHTML = (snapshot.val() && snapshot.val().companyName)||'Anonymous';
      document.getElementById("position").innerHTML = (snapshot.val() && snapshot.val().jobTitle)||'Anonymous';
      document.getElementById("email").innerHTML = email_id;
      document.getElementById("phone").innerHTML = (snapshot.val() && snapshot.val().mobileNumber)||'Anonymous';
      document.getElementById("Telephone").innerHTML = (snapshot.val() && snapshot.val().workTelephone)||'Anonymous';
      document.getElementById("loction").innerHTML = (snapshot.val() && snapshot.val().workAddress)||'Anonymous';

    });
   



    }

  } else {
    // No user is signed in.

    document.getElementById("user_div").style.display = "none";
    document.getElementById("login_div").style.display = "block";

  }
});

function login(){

  var userEmail = document.getElementById("email_field").value;
  var userPass = document.getElementById("password_field").value;

  firebase.auth().signInWithEmailAndPassword(userEmail, userPass).catch(function(error) {
    // Handle Errors here.
    var errorCode = error.code;
    var errorMessage = error.message;

    window.alert("Error : " + errorMessage);

    // ...
  });

}

function logout(){
  firebase.auth().signOut();
}
