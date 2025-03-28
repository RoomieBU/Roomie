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
    }

    // Match section

    // const [matches, setMatches] = useState([
    //     { id: 1, name: "Salvatore La Marca" }
    // ]);

    // const [likes, setLikes] = useState([
    //     {id: 1, name: "John Smith"},
    // ]);

    const [isMatchesVisible, setIsMatchesVisible] = useState(true);
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
    //     const getMatchList = async () => {
    //         try {
    //             const response = await fetch("https://roomie.ddns.net:8080/matches/getLikedList", {
    //                 method: "POST",
    //                 headers: { "Content-Type": "application/json" },
    //                 body: JSON.stringify({ token: localStorage.getItem("token") })
    //             });

    //             if (!response.ok) {
    //                 throw new Error("Failed to fetch potential roommate");
    //             }

    //             const result = await response.json();
    //             setLikedUsers(result)

    //         } catch (error) {
    //             console.error("Error fetching potential roommate:", error);
    //             // setError(error.message);
    //         }
    //     };

    //     getMatchList();
    //     console.log(likedUsers)
    // }

    return (
        <div className="sidebar">
            {(() => {
                switch (activeView) {
                    case "Chat":
                        return chatContacts.map(chat => (
                            <div className={`chatBox ${selectedChat === chat.id ? 'selected' : ''}`}
                                key={chat.id}
                                onClick={() => handleChatClick(chat.id)}>
                                <div className="profilePic"/>
                                <h3>{chat.name}</h3>
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