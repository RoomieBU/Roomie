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

    const [matches, setMatches] = useState([
        { id: 1, name: "Salvatore La Marca" },
        { id: 2, name: "Salvatore La Marca" },
        { id: 3, name: "Salvatore La Marca" },
        { id: 4, name: "Salvatore La Marca" },
        { id: 5, name: "Salvatore La Marca" },
        { id: 6, name: "Salvatore La Marca" },
        { id: 7, name: "Salvatore La Marca" }
    ]);

    const [likes, setLikes] = useState([
        {id: 1, name: "John Smith"},
        {id: 2, name: "John Doe"},
        {id: 3, name: "Jeff Man"}
    ]);

    return (
        <div className="sidebar">
            {/* <h1>{activeView}</h1> */}
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
                    case "Profile":
                        return (
                            <div className="profileBox">
                                <div className="settingTab">My Account</div>
                                <div className="settingTab">Preferences</div>
                                <div className="settingTab">Appearance</div>
                                <div className="settingTab">Log Out</div>
                            </div>
                        );
                    case "Match":
                        return (
                            <div className="matchBox">
                                <h4>Matches</h4>
                                <div className="line"/>
                                <div className="matchList">
                                    {matches.map(match => (
                                        <div key={match.id} className="selectedBox">
                                            <h4>{match.name}</h4>
                                        </div>
                                    ))}
                                </div>

                                <h4>Liked</h4>
                                <div className="line"/>
                                <div className="matchList">
                                    {likes.map(like => (
                                        <div key={like.id} className="selectedBox">
                                            <h4>{like.name}</h4>
                                        </div>
                                    ))}
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