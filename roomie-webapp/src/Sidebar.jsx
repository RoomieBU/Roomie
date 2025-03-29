import { useEffect, useState } from "react";
import PropTypes from 'prop-types';
import "./Sidebar.css"


function Sidebar({ currentView, onChatSelect }) {
    
    const [selectedChat, setSelectedChat] = useState(null)
    const [activeView, setActiveView] = useState(currentView || "Chat");
    
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
        console.log("CHAT ID: ", selectedChat)

        const selectedChat = "JEFF"
        onChatSelect(selectedChat)

        // const selectedContact = chatContacts.find(contact => contact.id === chatId);
        
        // if (onChatSelect && selectedContact) {
        //     onChatSelect(selectedContact);
        // }

        // console.log(groupChats)
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
        constructor(firstName, lastName, groupChatId, profilePicture) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.groupChatId = groupChatId;
            this.profilePicture = profilePicture
        }
    }

    const [userChats, setUserChats] = useState([])

    const parseGroupChats = async () => {
        if (!groupChats.length) return;
    
        const emailCount = new Set();
        let user = null;
    
        for (const { email1, email2 } of groupChats) {
            for (const email of [email1, email2]) {
                if (emailCount.has(email)) {
                    user = email;
                    break;
                }
                emailCount.add(email);
            }
            if (user) break;
        }
    
        if (!user) {
            console.error("User email not found in group chats.");
            return;
        }
    
        const userChatsTemp = await Promise.all(
            groupChats.map(async ({ groupchatId, email1, email2 }) => {
                let nonUserEmail = email1 === user ? email2 : email1;
                try {
                    const response = await fetch("https://roomie.ddns.net:8080/matches/getChatInformation", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ email: nonUserEmail })
                    });
    
                    if (!response.ok) {
                        throw new Error("Failed to fetch chat information");
                    }
    
                    const chatInformation = await response.json();
                    return new Chat(
                        chatInformation.first_name, 
                        chatInformation.last_name, 
                        groupchatId, 
                        chatInformation.profile_picture_url
                    );
                } catch (error) {
                    console.error("Error fetching chat information", error);
                    return null;
                }
            })
        );
    
        setUserChats(userChatsTemp.filter(chat => chat !== null));
    };
    


    useEffect(() => {
        if (activeView === "Chat") {
            const fetchChats = async () => {
                const chats = await getGroupchats();
                setGroupChats(chats);  // Store the data
            };
            fetchChats();
        }
    }, [activeView]);


    useEffect(() => {
        if (groupChats.length > 0) {
            parseGroupChats();
        }
    }, [groupChats]);

    return (
        <div onClick={() => parseGroupChats} className="sidebar">
            {(() => {
                switch (activeView) {
                    case "Chat":
                        return userChats.map(chat => (
                            <div className={`chatBox ${selectedChat === chat.groupChatId ? 'selected' : ''}`}
                                key={chat.groupChatId}
                                onClick={() => handleChatClick(chat.groupChatId)}>
                                <img
                                    src={chat.profilePicture}
                                    alt="P"
                                    className="profilePic"
                                />
                                <h3>{chat.firstName} {chat.lastName}</h3>
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