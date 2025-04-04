import { useEffect, useState } from "react";
import PropTypes from 'prop-types';
import "./Sidebar.css"
import Spinner from './Spinner'; // Ensure the correct path to Spinner component
import MatchWidget from "./MatchWidget";


function Sidebar({ currentView, onChatSelect}) {
    
    const [selectedChat, setSelectedChat] = useState(null)
    const [activeView, setActiveView] = useState(currentView || "Chat");
    
    // loading flag for spinner 
    const [loading, setLoading] = useState(true)

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

        const targetChat = userChats.find(chat => chat.groupChatId === chatId);
        console.log(targetChat)

        if (targetChat) {
            const data = [targetChat.firstName, targetChat.lastName, targetChat.groupChatId]
            onChatSelect(data); // Ensure onChatSelect expects an object
        }
    }

    // Match section

    const [isMatchesVisible, setIsMatchesVisible] = useState(true)

    function toggleMatches() {
        setIsMatchesVisible(!isMatchesVisible)
        // getLikedList()
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

            console.log("These are the groupchats: ", result)
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

    const [userChats, setUserChats] = useState([]);

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
        setLoading(false)
    };
    


    useEffect(() => {
        if (activeView === "Chat") {
            const fetchChats = async () => {
                setLoading(true)
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
                        if (loading) {
                            return <Spinner load="chats..."/>
                        } else {
                            return userChats.map(chat => (
                                <div className={`chatBox ${selectedChat === chat.groupChatId ? 'selected' : ''}`}
                                    key={chat.groupChatId}
                                    onClick={() => handleChatClick(chat.groupChatId)}>
                                    <img
                                        src={chat.profilePicture || `https://ui-avatars.com/api/?name=${chat.firstName[0]}${chat.lastName[0]}&background=random`}
                                        alt="P"
                                        className="profilePic"
                                    />
                                    <h3>{chat.firstName} {chat.lastName}</h3>
                                </div>
                            ));
                        }
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
                                }} className="container matchList">
                                    <div className="row">
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Emily" major="Biology" school="Bloomsburg University" age="22" aboutMe="Love hiking and coffee!" />
                                        </div>
                                        
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Sophia" major="Psychology" school="Bloomsburg University" age="23" aboutMe="Passionate about mental health advocacy." />
                                        </div>
                                        
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Ava" major="Art History" school="Bloomsburg University" age="22" aboutMe="Museum hopping is my thing!" />
                                        </div>
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Liam" major="Physics" school="Bloomsburg University" age="24" aboutMe="Stargazer and quantum physics fan." />
                                        </div>
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Daniel" major="Mathematics" school="Bloomsburg University" age="21" aboutMe="Chess lover and aspiring mathematician." />
                                        </div>
                                        <div className="col-6 col-md-6 d-flex justify-content-center align-items-center">
                                            <MatchWidget name="Jacob" major="Computer Science" school="Bloomsburg University" age="20" aboutMe="Tech enthusiast and gamer." />
                                        </div>
                                    </div>
                                </div>


                                
                                {/* <div style={{display: "flex", width: "140px", justifyContent: "space-evenly", }}>
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
                
                                </div> */}
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