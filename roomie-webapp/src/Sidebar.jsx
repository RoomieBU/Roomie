import { useEffect, useState } from "react";
import PropTypes from 'prop-types';
import "./Sidebar.css"


function Sidebar({ currentView, onChatSelect }) {
    
    const [selectedChat, setSelectedChat] = useState(null)
    const [activeView, setActiveView] = useState(currentView || "Chat");

    const chatContacts = [
        { id: 1, name: "Salvatore La Marca", lastMessage: "Hey, how are you?" },
        { id: 2, name: "Riley Simmons", lastMessage: "See you tomorrow!" },
        { id: 3, name: "Sam Kapp", lastMessage: "Thanks for the help" },
        { id: 4, name: "Emily Faso", lastMessage: "Did you get my email?" },
        { id: 5, name: "Matthew Yurkunas", lastMessage: "bruh"},
    ];

    

    useEffect(() => {
        // Update activeView when currentView prop changes
        if (currentView) {
            setActiveView(currentView);
        }
        
        const handleViewChange = (event) => {
            setActiveView(event.detail.view);
        };
        
        window.addEventListener('viewChange', handleViewChange);
        return () => {
            window.removeEventListener('viewChange', handleViewChange);
        };
    }, [currentView]); // Add currentView to dependency array

    const handleChatClick = (chatId) => {
        setSelectedChat(chatId);
        const selectedContact = chatContacts.find(contact => contact.id === chatId);
        
        if (onChatSelect && selectedContact) {
            onChatSelect(selectedContact);
        }

        // console.log(groupChats)
        parseGroupChats()
    }

    // Match section

    // const [matches, setMatches] = useState([
    //     { id: 1, name: "Salvatore La Marca" }
    // ]);

    // const [likes, setLikes] = useState([
    //     {id: 1, name: "John Smith"},
    // ]);

    const [isMatchesVisible, setIsMatchesVisible] = useState(true)
    const [isLikedVisible, setIsLikedVisible] = useState(true)

    function toggleMatches() {
        setIsMatchesVisible(!isMatchesVisible)
        // getLikedList()
    }

    function toggleLiked() {
        setIsLikedVisible(!isLikedVisible)
    }

    // const [likedUsers, setLikedUsers] = useState(null)

    // function getLikedList() {
        // const getMatchList = async () => {
        //     try {
        //         const response = await fetch("https://roomie.ddns.net:8080/matches/getLikedList", {
        //             method: "POST",
        //             headers: { "Content-Type": "application/json" },
        //             body: JSON.stringify({ token: localStorage.getItem("token") })
        //         });

        //         if (!response.ok) {
        //             throw new Error("Failed to fetch potential roommate");
        //         }

        //         const result = await response.json();
        //         setLikedUsers(result)

        //     } catch (error) {
        //         console.error("Error fetching potential roommate:", error);
        //         // setError(error.message);
        //     }
        // };

    //     getMatchList();
    //     console.log(likedUsers)
    // }


    // Get list of groupchats

    const [groupChats, setGroupChats] = useState([]);

    const getGroupchats = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getGroupchats", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });

            if(!response.ok) {
                throw new Error("Failed to fetch groupchats");
            }

            const result = await response.json();
            return result

        } catch(error) {
            console.error("Error fetching groupchats: ", error)
        }
    }

    class Chat {
        constructor(email, profilePicture, groupchatId) {
            this.email = email;
            this.profilePicture = profilePicture;
            this.groupchatId = groupchatId;
        }
    }

    // parse groupchats to get users emails
    function parseGroupChats() {

        const emailCount = new Set()
        let user = null

        // this is just an easy way to get the current user's email --> change later
        for (const {email1, email2} of groupChats) {
            for (const email of [email1, email2]) {
                if (emailCount.has(email)) {
                    user = email; 
                    break
                }
                emailCount.add(email);
            }
            if(user) break
        }

        // create chat objects to make visuals
        const userChats = []
        
        for(const {groupchatId, email1, email2} of groupChats) {
            let nonUserEmail = null
            console.log(groupchatId)

            if (email1 === user) {
                nonUserEmail = email2;
            } else {
                nonUserEmail = email1;
            }

            // search for nonUserEmail's profile picture here

            const getChatInformation = async () => {
                try {
                    const response = await fetch("https://roomie.ddns.net:8080/matches/getChatInformation", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ email: nonUserEmail })
                    });
    
                    if (!response.ok) {
                        throw new Error("Failed to fetch profile picture of given email");
                    }

                    const result = await response.json();
                    return result
                } catch (error) {
                    console.error("Error fetching profile picture", error);
                }
            };
    
            const chatInformation = getChatInformation();

            // create chat object
            userChats.push(new Chat(nonUserEmail, chatInformation.profile_picture_url, groupchatId))
        }

        console.log("Helloooooo", userChats)
        console.log(groupChats)

    }


    useEffect(() => {
        if (activeView === "Chat") {
            const fetchChats = async () => {
                const chats = await getGroupchats();
                setGroupChats(chats);  // Store the data
            };
            fetchChats();
        }
    }, [activeView]);

    return (
        <div className="sidebar">
            {(() => {
                switch (activeView) {
                    case "Chat":
                        return groupChats.map(chat => (
                            <div className={`chatBox ${selectedChat === chat.groupChatId ? 'selected' : ''}`}
                                key={chat.groupChatId}
                                onClick={() => handleChatClick(chat.groupChatId)}>
                                <div className="profilePic"/>
                                <h3>{chat.email2}</h3>
                            </div>
                        ));
                    case "Match":
                        return (
                            <div className="matchBox">
                               
                                <div style={{display: "flex", width: "140px", justifyContent: "space-evenly", }}>
                                    {isMatchesVisible ? <i style={{marginTop: "3px"}} className="bi bi-chevron-down"/> : <i style={{marginTop: "3px"}} className="bi bi-chevron-right"/>}  
                                    <h4 onClick={toggleMatches} > Matches</h4>
                                </div>
                                
                                <div className="line"/>
                                <div style={{
                                        height: isMatchesVisible ? "auto" : "0px",
                                        overflow: "scroll",
                                        transition: "height 0.3s ease-in-out",
                                        visibility: isMatchesVisible ? "visible" : "hidden"
                                    }} className="matchList">
                                    {/* {matches.map(match => (
                                        <div key={match.id} className="selectedBox">
                                            <h4>{match.name}</h4>
                                        </div>
                                    ))} */}
                                </div>
                                
                                <div style={{display: "flex", width: "140px", justifyContent: "space-evenly", }}>
                                {isLikedVisible ? <i style={{marginTop: "3px"}} className="bi bi-chevron-down"/> : <i style={{marginTop: "3px"}} className="bi bi-chevron-right"/>} 
                                    <h4 onClick={toggleLiked}>Liked</h4>
                                </div>
                                <div className="line"/>
                                <div style={{
                                        height: isLikedVisible ? "auto" : "0px",
                                        overflow: "scroll",
                                        transition: "height 0.3s ease-in-out",
                                        visibility: isLikedVisible ? "visible" : "hidden"
                                    }} className="matchList">
                                    {/* {likes.map(like => (
                                        <div key={like.id} className="selectedBox">
                                            <h4>{like.name}</h4>
                                        </div>
                                    ))} */}
                                </div>
                            </div>
                        );
                    
                    default:
                        return <div>Welcome!</div>;
                }
            })()}
        </div>
    );
}

Sidebar.propTypes = {
    currentView: PropTypes.string,
    onChatSelect: PropTypes.func  // Add this line
};

export default Sidebar;