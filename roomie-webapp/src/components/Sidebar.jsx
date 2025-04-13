import { useEffect, useState } from "react";
import PropTypes from 'prop-types';
import "./Sidebar.css";
import Spinner from './Spinner';
import MatchWidget from "./MatchWidget";

function Sidebar({ currentView, onChatSelect }) {
    const [userEmail, setUserEmail] = useState("");
    const [selectedChat, setSelectedChat] = useState(null);
    const [activeView, setActiveView] = useState(currentView || "Chat");
    const [loading, setLoading] = useState(true);
    const [groupChats, setGroupChats] = useState([]);
    const [userChats, setUserChats] = useState([]);
    const [groupChatMode, setGroupChatMode] = useState(false);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [isMatchesVisible, setIsMatchesVisible] = useState(true);
    const [matchList, setMatchList] = useState([])

    // Sync active view with props
    useEffect(() => {
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
    }, [currentView]);


    const fetchUserEmail = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/profile/getUserEmail", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });

            if (!response.ok) throw new Error("Failed to fetch user email");
            const result = await response.json();
            setUserEmail(result.email);
        } catch (error) {
            console.error("Error fetching user email: ", error);
        }
    };

    const fetchChats = async () => {
        setLoading(true);
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getGroupchats", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });
    
            if (!response.ok) throw new Error("Failed to fetch groupchats");
            const result = await response.json();
            setGroupChats(result);
        } catch (error) {
            console.error("Error fetching groupchats: ", error);
        } finally {
            setLoading(false);
        }
    };

    // Fetch group chats only when in "Chat" view
    useEffect(() => {
        if (activeView === "Chat") {
            fetchChats();
        }
        if (activeView === "Match") {
            getMatchList();
            
        }
        fetchUserEmail();
    }, [activeView]);

    // Parse group chats after both userEmail and groupChats are loaded

    const parseGroupChats = async () => {
        const Chat = function (firstName, lastName, groupChatId, profilePicture) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.groupChatId = groupChatId;
            this.profilePicture = profilePicture;
        };

        const userChatsTemp = await Promise.all(
            groupChats.map(async ({ groupchatId, email1, email2, email3, email4, email5, email6 }) => {
                const emails = { email1, email2, email3, email4, email5, email6 };
                const hold = [];

                if (email3) {
                    for (let i = 1; i <= 6; i++) {
                        const email = emails[`email${i}`];
                        if (!email || email === userEmail) continue;

                        try {
                            const response = await fetch("https://roomie.ddns.net:8080/matches/getChatInformation", {
                                method: "POST",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify({ email })
                            });

                            if (!response.ok) throw new Error("Failed to fetch chat info");

                            const chatInfo = await response.json();
                            hold.push(new Chat(
                                chatInfo.first_name,
                                chatInfo.last_name,
                                groupchatId,
                                chatInfo.profile_picture_url
                            ));
                        } catch (error) {
                            console.error("Error fetching chat information", error);
                            return null;
                        }
                    }

                    const firstNames = hold.map(chat => chat.firstName);
                    let name = "";

                    if (firstNames.length === 1) {
                        name = firstNames[0];
                    } else if (firstNames.length === 2) {
                        name = `${firstNames[0]} and ${firstNames[1]}`;
                    } else {
                        const last = firstNames.pop();
                        name = `${firstNames.join(", ")}, and ${last}`;
                    }

                    return new Chat(
                        name,
                        "",
                        groupchatId,
                        `https://api.dicebear.com/7.x/thumbs/svg?seed=${encodeURIComponent(name)}`
                    );
                } else {
                    const nonUserEmail = email1 === userEmail ? email2 : email1;
                    try {
                        const response = await fetch("https://roomie.ddns.net:8080/matches/getChatInformation", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({ email: nonUserEmail })
                        });

                        if (!response.ok) throw new Error("Failed to fetch chat info");

                        const chatInfo = await response.json();
                        return new Chat(
                            chatInfo.first_name,
                            chatInfo.last_name,
                            groupchatId,
                            chatInfo.profile_picture_url
                        );
                    } catch (error) {
                        console.error("Error fetching chat information", error);
                        return null;
                    }
                }
            })
        );

        setUserChats(userChatsTemp.filter(chat => chat !== null));
        setLoading(false);
    };

    useEffect(() => {
        if (!userEmail || groupChats.length === 0) return;
        parseGroupChats();
    }, [userEmail, groupChats]);

    const handleChatClick = (chatId) => {
        const targetChat = userChats.find(chat => chat.groupChatId === chatId);
        if (!targetChat) return;
    
        if (groupChatMode) {
            setSelectedUsers(prev => {
                const alreadySelected = prev.some(user => user.groupChatId === targetChat.groupChatId);
    
                if (alreadySelected) {
                    // Remove if already selected
                    return prev.filter(user => user.groupChatId !== targetChat.groupChatId);
                } else if (prev.length >= 6) {
                    // Do not add if limit reached
                    return prev;
                } else {
                    // Add if under limit
                    return [...prev, targetChat];
                }
            });
        } else {
            setSelectedChat(chatId);
            const data = [targetChat.firstName, targetChat.lastName, targetChat.groupChatId];
            onChatSelect(data);
        }
    };
    

    function toggleMatches() {
        setIsMatchesVisible(!isMatchesVisible);
    }

    function handleGroupchatCreation() {
        const createGroupChat = async () => {
            const body = JSON.stringify({
                token: localStorage.getItem("token"),
                ...Object.fromEntries(
                    selectedUsers.map((user, i) => [`groupChatId${i}`, user.groupChatId])
                ),
            });
    
            try {
                // Create the group chat on the backend
                const response = await fetch("https://roomie.ddns.net:8080/chat/createGroupChat", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body
                });
    
                // Check if the response was successful
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || "Failed to create groupchat");
                }
    
                // Get the newly created group chat data from the response
                const newGroupChat = await response.json();
    
                // Wait for the backend confirmation before updating state
                setGroupChats(prevChats => {
                    // Add the new group chat at the beginning (or end)
                    return [newGroupChat, ...prevChats];
                });
    
                // Re-run parseGroupChats to reformat the new group chat data
                await parseGroupChats(); // Ensure this runs synchronously
    
            } catch (error) {
                console.error("Error creating groupchat", error);
            } finally {
                setSelectedUsers([]);
                setGroupChatMode(false);
            }
        };
    
        createGroupChat();
    }

    function handleGroupchatCancel() {
        setGroupChatMode(false);
        setSelectedUsers([]);
    }

    // get match list information
    const getMatchList = async () => {
        setLoading(true);
        try {
            const response = await fetch("https://roomie.ddns.net:8080/matches/getMatchList", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });
    
            if (!response.ok) throw new Error("Failed to fetch match list");
            const result = await response.json();
            setMatchList(result)
            console.log("MATCH LIST:", result)
        } catch (error) {
            console.error("Error fetching match list: ", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            {activeView === "Chat" && <div className="groupchat-cluster">
                {groupChatMode && (
                    <>
                        <h4>Select users for your groupchat</h4>
                        <button className="btn purpleBtn" onClick={handleGroupchatCancel}>Cancel</button>
                        <button className="btn purpleBtn" onClick={handleGroupchatCreation}>Confirm</button>
                    </>
                )}
                <i onClick={() => setGroupChatMode(true)} className="bi bi-chat-dots groupChatButton" />
            </div>}
            <div className="sidebar">
                {(() => {
                    switch (activeView) {
                        case "Chat":
                            return loading ? (
                                <Spinner load="chats..." />
                            ) : (
                                <>
                                    {userChats.map(chat => {
                                        const isSelected = selectedUsers.some(user => user.groupChatId === chat.groupChatId);
                                        return (
                                            <div
                                                className={`chatBox ${selectedChat === chat.groupChatId ? 'selected' : ''} ${isSelected ? 'gSelected' : ''}`}
                                                key={chat.groupChatId}
                                                onClick={() => handleChatClick(chat.groupChatId)}
                                            >
                                                <img
                                                    src={chat.profilePicture}
                                                    alt="P"
                                                    className="profilePic"
                                                />
                                                <h3>{chat.firstName} {chat.lastName}</h3>
                                            </div>
                                        );
                                    })}
                                </>
                            );

                        case "Match":
                            return (
                                <div className="matchBox">
                                    <div style={{ display: "flex", width: "140px", justifyContent: "space-evenly" }}>
                                        {isMatchesVisible ? <i className="bi bi-chevron-down" /> : <i className="bi bi-chevron-right" />}
                                        <h4 onClick={toggleMatches}>Matches</h4>
                                    </div>
                                    <div className="line" />
                                    <div style={{
                                        height: isMatchesVisible ? "auto" : "0px",
                                        overflow: "scroll",
                                        transition: "height 0.3s ease-in-out",
                                        visibility: isMatchesVisible ? "visible" : "hidden"
                                    }} className="container matchList">
                                        <div className="row">
                                            {matchList.map(match => (
                                                <MatchWidget
                                                key={match.userId}
                                                firstName={match.firstName}
                                                lastName={match.lastName}
                                                major={match.major}
                                                school={match.school}
                                                age={match.dateOfBirth}
                                                aboutMe={decodeURIComponent(match.aboutMe)}
                                                picture={match.profilePicture}
                                                />
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            );

                        default:
                            return <div>Welcome!</div>;
                    }
                })()}
            </div>
        </>
    );
}

Sidebar.propTypes = {
    currentView: PropTypes.string,
    onChatSelect: PropTypes.func
};

export default Sidebar;
